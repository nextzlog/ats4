package models

import play.api.db.Database

object Dupe {
	def delete(post: Post)(implicit db: Database) = {
		// delete conflicting entries
		for(old <- Post.ofCall(post.call) if(Conf.sectsRC.contains(old.sect))) old.delete
		// delete conflicting entries
		for(old <- Post.ofCall(post.call) if(Conf.sectsAM.contains(old.sect))) {
			if(!Conf.sectsPM.contains(post.sect)) old.delete
		}
		// delete conflicting entries
		for(old <- Post.ofCall(post.call) if(Conf.sectsPM.contains(old.sect))) {
			if(!Conf.sectsAM.contains(post.sect)) old.delete
		}
		// delete SOUGOU entry once
		for(old <- Post.ofCall(post.call) if(old.sect.contains("総合部門"))) old.delete
	}
}
