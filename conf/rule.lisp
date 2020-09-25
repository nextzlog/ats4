;; ALLJA1 CONTEST DEFINED by ATS-4

(load "qxsl/ruler/common.lisp")

(import java.time.DayOfWeek)
(import java.time.LocalDate)
(import java.time.Month)
(import java.time.temporal.TemporalAdjuster)
(import java.time.temporal.TemporalAdjusters)

; contest settings
(defun NAME () "ALLJA1")
(defun HOST () "東大無線部")
(defun MAIL () "allja1@ja1zlo.u-tokyo.org")
(defun SITE () "ja1zlo.u-tokyo.org")
(defun RULE () "ja1zlo.u-tokyo.org/allja1/%02drule.html")

; output directory
(defun output () "rcvd")
(defun report () "report.csv")

(defun date (year month dayOfWeek nth)
	((method 'with LocalDate TemporalAdjuster)
		((method 'of LocalDate int Month int)
			null year ((method 'valueOf Month String) null month) 1)
		((method 'dayOfWeekInMonth TemporalAdjusters int DayOfWeek)
			null nth ((method 'valueOf DayOfWeek String) null dayOfWeek))))

; schedule
(defun starting year (date year "JUNE" "SATURDAY" 4))
(defun deadline year (date year "JULY" "SATURDAY" 3))

; email
(defun mail-interval-minutes () 5)

; routines for ATS-4
(defmacro 総合部門の選択 sects
	`(block
		(setq sects (dolist (s (list ,@sects)) (split s " ")))
		(setq corps (cond
			((member 団 (mapcar cadr sects)) 団)
			((member 個 (mapcar cadr sects)) 個)))
		(setq areas (cond
			((member 内 (mapcar car  sects)) 内)
			((member 外 (mapcar car  sects)) 外)))
		(format "%s %s %s 部門" areas corps 総合)))

(defun 部門と運用地の検査 (部門 市区町村)
	(xor
		(equal (car (split 部門 " ")) 外)
		(equal (city-region (city "area" (car (split 市区町村 "(?<=都|道|府|県)"))) 2) "関東")))

(load "qxsl/ruler/allja1.lisp")
