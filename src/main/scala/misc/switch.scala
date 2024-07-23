package misc

import chisel3._
import chisel3.util.Fill
import chisel3.util.Cat

class Switch extends Module {
  val in = IO(new Bundle {
    val addr = Input(UInt(12.W))
    val switch = Input(UInt(24.W))
  })
  val out = IO(new Bundle {
    val rdata = Output(UInt(32.W))
  })
  when(reset.asBool) {
    out.rdata := 0.U
  }.otherwise {
    out.rdata := Cat(Fill(8, 0.U), in.switch)
  }
}
