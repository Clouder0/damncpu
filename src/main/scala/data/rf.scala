package data

import chisel3._
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

object RF {
  object SEL {
    val ALU = 0.U
    val DRAM = 1.U
    val IMM = 2.U
    val PC4 = 3.U

  }
}

@instantiable
class RF extends Module {
  @public val in = IO(new Bundle {
    val rR1 = Input(UInt(5.W))
    val rR2 = Input(UInt(5.W))
    val wR = Input(UInt(5.W))
    val wD = Input(UInt(32.W))
    val we = Input(Bool())
  })

  @public val out = IO(new Bundle {
    val rD1 = Output(UInt(32.W))
    val rD2 = Output(UInt(32.W))
  })

  val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  out.rD1 := regs(in.rR1)
  out.rD2 := regs(in.rR2)

  when(in.we && in.wR =/= 0.U) { // disallow write to x0
    regs(in.wR) := in.wD
  }
}
