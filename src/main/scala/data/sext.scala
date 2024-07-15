package data

import chisel3._
import chisel3.util.Cat
import chisel3.util.Fill

object SEXT {
  object OP {

    val I = 0.U
    val S = 1.U
    val B = 2.U
    val U = 3.U
    val J = 4.U
    val SHIFT = 5.U
  }
}
class SEXT extends Module {
  val io = IO(new Bundle {
    val din = Input(UInt(25.W))
    val op = Input(UInt(3.W))
    val dout = Output(UInt(32.W))
  })
  when(io.op === SEXT.OP.I) {
    io.dout := Cat(Fill(20, io.din(24)), io.din(24, 13))
  }.elsewhen(io.op === SEXT.OP.S) {
    io.dout := Cat(Fill(20, io.din(24)), io.din(24, 18), io.din(4, 0))
  }.elsewhen(io.op === SEXT.OP.B) {
    io.dout := Cat(Fill(20, io.din(24)), io.din(24, 18), io.din(4, 1), 1.U)
  }.elsewhen(io.op === SEXT.OP.U) {
    io.dout := Cat(io.din(24, 5), Fill(12, 0.U))
  }.elsewhen(io.op === SEXT.OP.J) {
    io.dout := Cat(Fill(27, 0.U), io.din(17, 13))
  }.otherwise {
    io.dout := Fill(32, 0.U)
  }
}
