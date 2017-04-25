(ns axrs.re-frame.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [axrs.re-frame.http-fxs-test]))

(doo-tests 'axrs.re-frame.http-fxs-test)
