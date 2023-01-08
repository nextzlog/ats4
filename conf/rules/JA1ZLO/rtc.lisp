;; REAL-TIME CONTEST DEFINED by 無線部開発班

(load "qxsl/ruler/online.lisp")

(setq RT (contest "リアルタイムコンテスト"))

(SinOp (個 電信) 電信 cities (SinOp? band? area? MORSE?))
(SinOp (個 電話) 電話 cities (SinOp? band? area? PHONE?))
(SinOp (個 電電) 電電 cities (SinOp? band? area? CW/PH?))
(MulOp (団 電信) 電信 cities (MulOp? band? area? MORSE?))
(MulOp (団 電話) 電話 cities (MulOp? band? area? PHONE?))
(MulOp (団 電電) 電電 cities (MulOp? band? area? CW/PH?))
