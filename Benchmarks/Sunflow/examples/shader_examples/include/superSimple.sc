%% common settings

image {
   resolution 128 128
   aa 0 2
}

accel bih
filter mitchell
bucket 32 row


%% camera

camera {
   type pinhole
   eye    3.27743673325 -9.07978439331 9.93055152893
   target 0 0 0
   up     0 0 1
   fov    40
   aspect 1
}


%% light sources

light {
   type meshlight
   name "meshlight.001"
   emit 1 1 1
   radiance 15
   samples 32
   points 4
      -4.25819396973 -4.8784570694 5.70054674149
      -5.13696432114 -5.61583280563 4.06224298477
      -6.422539711 -4.08374404907 4.06224298477
      -5.54376888275 -3.34636831284 5.70054721832
   triangles 2
      0 1 2
      0 2 3
}


%% geometry

object {
   shader ground
   type generic-mesh
   name "Plane"
   points 8
       3.1  3.1 0
       3.1 -3.1 0
      -3.1 -3.1 0
      -3.1  3.1 0
      -3.1  3.1 -0.61
      -3.1 -3.1 -0.61
       3.1 -3.1 -0.61
       3.1  3.1 -0.61
/*
      2.99000000954 2.98999977112 0
      2.99000000954 -2.99000000954 0
      -2.99000024796 -2.9899995327 0
      -2.98999905586 2.99000096321 0
      -2.98999905586 2.99000096321 -0.611932575703
      -2.99000024796 -2.9899995327 -0.611932575703
      2.99000000954 -2.99000000954 -0.611932575703
      2.99000000954 2.98999977112 -0.611932575703
*/
   triangles 12
      0 3 2
      0 2 1
      2 3 4
      2 4 5
      3 0 7
      3 7 4
      0 1 6
      0 6 7
      1 2 5
      1 5 6
      5 4 7
      5 7 6
   normals none
   uvs none
}

object {
  shader "shader01"
  type sphere
  c -2 2 0.7
  r 0.7
}
