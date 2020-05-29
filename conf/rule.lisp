;; ALLJA1 CONTEST DEFINED by ATS-4

(load "qxsl/ruler/macros.lisp")

; contest settings
(defun name () "ALLJA1")
(defun host () "東大無線部")
(defun mail () "allja1@ja1zlo.u-tokyo.org")
(defun site () "ja1zlo.u-tokyo.org")
(defun rule () "ja1zlo.u-tokyo.org/allja1/%02drule.html")

; output directory
(defun output () "rcvd")
(defun report () "report.csv")

; schedule
(defun starting year (date year "JUNE" "SATURDAY" 4))
(defun deadline year (date year "JULY" "SATURDAY" 3))

; routines for ATS-4
(defmacro 総合部門の選択 sects
	`(progn
		(setq sects (dolist (s (list ,@sects)) (split " " s)))
		(setq corps (cond (
			((member 団 (mapcar cadr sects)) 団)
			((member 個 (mapcar cadr sects)) 個))))
		(setq areas (cond (
			((member 内 (mapcar car  sects)) 内)
			((member 外 (mapcar car  sects)) 外))))
		(cat areas corps 総合 "部門")))

(defun 部門と運用地の検査 (部門 市区町村)
	(xor
		(equal (car (split " " 部門)) 外)
		(equal (city "area" (car (split "(?<=都|道|府|県)" 市区町村)) 2) "関東")))

(load "qxsl/ruler/allja1.lisp")
