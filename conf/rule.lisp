;; ALLJA1 CONTEST DEFINED by ATS-4

(load "qxsl/ruler/radial.lisp")

(import java.time.Month)
(import java.time.DayOfWeek)
(import java.time.LocalDate)
(import java.time.temporal.TemporalAdjusters)

; contest settings
(defun name () "ALLJA1")
(defun host () "東大無線部")
(defun mail () "allja1@ja1zlo.u-tokyo.org")
(defun site () "ja1zlo.u-tokyo.org")
(defun rule () "ja1zlo.u-tokyo.org/allja1/%02drule.html")

; output directory
(defun output () "rcvd")
(defun report () "report.csv")

(defun date (year month dayOfWeek nth)
	((access LocalDate 'with)
		((access LocalDate 'of) null year
			((access Month 'valueOf) null month) 1)
		((access TemporalAdjusters 'dayOfWeekInMonth) null nth
			((access DayOfWeek 'valueOf) null dayOfWeek))))

; schedule
(defun starting year (date year "JUNE" "SATURDAY" 4))
(defun deadline year (date year "JULY" "SATURDAY" 3))

; routines for ATS-4
(defmacro 総合部門の選択 sects
	`(block
		(setq sects (dolist (s (list ,@sects)) (split s " ")))
		(setq corps (cond (
			((member 団 (mapcar cadr sects)) 団)
			((member 個 (mapcar cadr sects)) 個))))
		(setq areas (cond (
			((member 内 (mapcar car  sects)) 内)
			((member 外 (mapcar car  sects)) 外))))
		(cat areas corps 総合 "部門")))

(defun 部門と運用地の検査 (部門 市区町村)
	(xor
		(equal (car (split 部門 " ")) 外)
		(equal (city "area" (car (split 市区町村 "(?<=都|道|府|県)")) 2) "関東")))

; assign ALLJA1 rules to the variable JA1
(setq JA1 (load "qxsl/ruler/allja1.lisp"))

; remove 団体部門s
(dolist (sect (array JA1)) (if (. contains (. getName sect ()) "団体") (. remove JA1 (sect))))

JA1
