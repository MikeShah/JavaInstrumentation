shader {
  name ground
  type amb-occ
  bright   1 0 1
  dark     0 0 0
  samples  64
  dist     6
}

shader {
  name "shader01"
  type amb-occ
  bright   1 0.5 0
  dark     .3 .1 0
  samples  1
  dist     6
}

include include/superSimple.sc
