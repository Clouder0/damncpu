package misc

import chisel3._
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public
import chisel3.experimental.hierarchy.Instantiate
import chisel3.util.Counter
import chisel3.util.Cat
import chisel3.util.Fill

@instantiable
class Digit extends Module {

  @public val in = IO(new Bundle {
    val digit = Input(UInt(4.W))
  })

  @public val out = IO(new Bundle {
    val seg = Output(UInt(8.W))
  })

  val segs = Wire(Vec(16, UInt(8.W)))
  segs(0) := "b00000011".U
  segs(1) := "b10011111".U
  segs(2) := "b00100101".U
  segs(3) := "b00001101".U
  segs(4) := "b10011001".U
  segs(5) := "b01001001".U
  segs(6) := "b01000001".U
  segs(7) := "b00011111".U
  segs(8) := "b00000001".U
  segs(9) := "b00011001".U
  segs(10) := "b00010001".U
  segs(11) := "b11000001".U
  segs(12) := "b11100101".U
  segs(13) := "b10000101".U
  segs(14) := "b01100001".U
  segs(15) := "b01110001".U
  out.seg := segs(in.digit)
}

class LEDLoopDisplay(refresh_time: Int) extends Module {
  val in = IO(new Bundle {
    val addr = Input(UInt(12.W))
    val we = Input(Bool())
    val wdata = Input(UInt(32.W))
  })

  val out = IO(new Bundle {
    val en_sel = Output(UInt(8.W))
    val seg = Output(UInt(8.W))
  })

  val display_data = RegInit(0.U(32.W))
  // 4bits per digit
  val segs_to_display = Wire(Vec(8, UInt(8.W)))
  for (i <- 0 until 8) {
    val digit_display = Instantiate(new Digit)
    digit_display.in.digit := display_data(4 * i + 3, 4 * i)
    segs_to_display(i) := digit_display.out.seg
  }

  val refresher = Counter(refresh_time)
  val idx = RegInit(0.U(3.W))
  when(refresher.inc()) {
    idx := idx + 1.U
    when(idx === 7.U) {
      idx := 0.U
    }
  }
  out.seg := segs_to_display(idx)
  out.en_sel := ~(1.U << idx)

  when(in.we) {
    display_data := in.wdata
  }
}
