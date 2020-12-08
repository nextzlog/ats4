package controllers

import java.nio.charset.StandardCharsets.UTF_8
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.concurrent.duration._

import play.api.Configuration
import play.api.libs.streams.ActorFlow
import play.api.mvc.Results.Forbidden
import play.api.mvc.WebSocket

import models.{Person, Receiver, Schedule}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub}

@Singleton class Agent @Inject()(implicit as: ActorSystem, mat: Materializer, cfg: Configuration) {
	val (sink,src) = MergeHub.source[Array[Byte]].toMat(BroadcastHub.sink)(Keep.both).run()
	val bus = Flow.fromSinkAndSource(sink, src).delay(cfg.get[Int]("rtc.delay").second)
	case class Agency(out: ActorRef, call: String) extends Actor {
		override def receive = {
			case msg: Array[Byte] => out! new Receiver().push(call, msg).getBytes(UTF_8)
		}
	}
	def accept(call: String, uuid: String) = Person.findAllByCall(call).find(_.uuid.toString == uuid).isDefined
	def socket(call: String, uuid: String) = WebSocket.acceptOrResult[Array[Byte], Array[Byte]] {
		req => Future.successful(if(Schedule.accept && accept(call, uuid)) {
			Right(ActorFlow.actorRef(out => Props(Agency(out, call))).viaMat(bus)(Keep.right))
		} else {
			Left(Forbidden)
		})
	}
}
