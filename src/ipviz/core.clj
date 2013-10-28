(ns ipviz.core
  (:use 
     clojure.java.io
     seesaw.graphics 
     seesaw.color)
  (:import [javax.imageio.ImageIO])
  )

; example IP: fcd2:b843:787a:59f3:6345:7ac2:6df3:5523
; I'm calling the four-digit hexadecimal 

(def PI Math/PI)
(def TWO-PI (* 2 Math/PI))
(def ONE-HEX-RAD (/ TWO-PI 16))

(defn hex2rad [hex] (* ONE-HEX-RAD hex))

(comment defn vertex-at-rad [rad radius]
  (let [x (* radius (Math/cos rad)) 
        y (* radius (Math/sin rad))] 
  (vertex x y)))



(comment defn draw-ip-quad
  "draw an ip "
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics radus a b c d]
  (let [rads (map hex2rad (vec  (sort '(a b c d))))]
    (stroke 0 220 20 100)
    (fill 0 220 20 100)
    (begin-shape)
    (vertex-at-rad a radius)
    (vertex-at-rad b radius)
    (vertex-at-rad c radius)
    (vertex-at-rad d radius)
    (vertex-at-rad a radius)
    (end-shape)
    (no-fill)
    (ellipse 0 0 200 200)
    )
  )

(defn provide-default-hints [hints]
  (let [dhints {:size 100
                :radius (- (* 0.5 (or (:size hints) 100)) 1)
                :fgcolor (color "green" 128)
                :bgcolor (color "lightgreen" 128)}]
    (conj dhints hints)))

(defn hex-to-radian [h]
  (let [wedge (/ 360 17)
        h     (Integer/parseInt (str h) 16) ] 
    (Math/toRadians (* h wedge))))

(defn octgroup-to-radians [g] 
  (vec (map hex-to-radian g)))

(defn radian-to-xy [d radius] [(* radius (Math/cos d)) (* radius (Math/sin d))])

(defn radgroup-to-polygon [g radius]
 (map radian-to-xy g (repeat radius))
  )

(defn draw-ip-polygon [ip6 & hints]
  (let [hints (apply provide-default-hints hints)
        {:keys [size radius fgcolor bgcolor]} hints
        center (* 0.5 size)
        img (buffered-image size size) 
        gc (.createGraphics img)
        wedge (/ 360 17) 
;        octgroup (map vec (clojure.string/split ip6))
        ip6polygons (doall (map radgroup-to-polygon (map octgroup-to-radians (map vec (clojure.string/split ip6  #":"))) (repeat radius)))
        s1 (style :foreground "red" :background (color "darkred" 160))
        s2 (style :foreground fgcolor :background bgcolor)
        s3 (style :foreground "red" :background bgcolor)
        s4 (style :foreground fgcolor :stroke (stroke :width 10))
        firstone (first (first ip6polygons))
        ]
    (push gc 
          (translate gc center center)
          (rotate gc -90)
          (draw gc (apply circle (conj firstone 50)) s1)
          (draw gc (circle 0 0 radius) s4)
          (apply (partial draw gc) (interleave (doall (map (partial apply polygon) ip6polygons)) (repeat s2)))
          ((partial draw gc) (apply polygon (first ip6polygons)) s1)
          )
    img)

  )

(comment
(provide-default-hints {:size 500})   
(do (javax.imageio.ImageIO/write (draw-ip-polygon "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" {:size 1024}) "png" (file "test-images/vermouth.png"))
(javax.imageio.ImageIO/write (draw-ip-polygon "fcdb:158c:ef6c:dfea:000f:bd01:a738:895f" {:size 1024}) "png" (file "test-images/absinthe.png"))
(javax.imageio.ImageIO/write (draw-ip-polygon "fce3:b909:def1:aa07:2efa:4314:e83e:c3dc" {:size 1024}) "png" (file "test-images/olive.png"))
(javax.imageio.ImageIO/write (draw-ip-polygon "fcd4:eeaf:b23e:08c9:ab84:d0cf:3f64:c55d" {:size 1024}) "png" (file "test-images/lime.png"))
(javax.imageio.ImageIO/write (draw-ip-polygon "fcb1:2730:7571:c1bc:d50b:ceb7:f25c:9b2b" {:size 1024}) "png" (file "test-images/brendan-erwin-SEA.png"))
(javax.imageio.ImageIO/write (draw-ip-polygon "fc32:c50f:9ed5:8fd8:7efd:cbfe:384a:9e8d" {:size 1024}) "png" (file "test-images/brendan-erwin-ATL.png"))
(javax.imageio.ImageIO/write (draw-ip-polygon "fcfb:6c6f:fcd4:0d83:79e1:50ff:ca02:8c24" {:size 1024}) "png" (file "test-images/burrito.png")))

(clojure.string/split  "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" #":")
(map vec (clojure.string/split  "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" #":"))
(map octgroup-to-radians (map vec (clojure.string/split  "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" #":")))
(map (partial println "this:" ) (map radgroup-to-polygon (map octgroup-to-radians (map vec (clojure.string/split  "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" #":"))) (repeat 49)))
(map radgroup-to-polygon (map octgroup-to-radians (map vec (clojure.string/split  "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" #":"))) (repeat 49))
(map (partial apply polygon ) (map radgroup-to-polygon (map octgroup-to-radians (map vec (clojure.string/split  "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" #":"))) (repeat 49)))
(let [i6p  (map radgroup-to-polygon (map octgroup-to-radians (map vec (clojure.string/split  "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" #":"))) (repeat 4 49))]
  (interleave  (map (partial apply polygon) i6p ) (repeat (style :foreground "blue")))
  )
(polygon [36.211436943812274 -33.01108653868133]  [-13.409486513532073 -47.129456515468135]  [4.521149613701732 -48.7909746384567]  [36.211436943812295 33.011086538681305])
(color "lightgreen" 128)
(map * [2 4 6] (repeat 3 10))
  
(Integer/parseInt (str \a) 16)
(hex-to-degree \0)
(hex-to-degree \a)
(hex-to-radian \a)
(octgroup-to-degree [ \a \f \1 \0])
(Math/cos 10)
(radian-to-xy 1 100)

)

(comment --------------------------------------------------------)


(defn -main
  [& args]

  )
