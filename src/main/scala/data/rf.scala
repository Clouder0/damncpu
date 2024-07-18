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
    val we = Input(Bool())
    val wsel = Input(UInt(2.W))
    val from_alu = Input(UInt(32.W))
    val from_dram = Input(UInt(32.W))
    val from_imm = Input(UInt(32.W))
    val from_pc = Input(UInt(32.W))
  })

  @public val out = IO(new Bundle {
    val rD1 = Output(UInt(32.W))
    val rD2 = Output(UInt(32.W))
    val wD = Output(UInt(32.W))
  })

  val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  out.rD1 := regs(in.rR1)
  out.rD2 := regs(in.rR2)

  when(in.wsel === RF.SEL.ALU) {
    out.wD := in.from_alu
  }.elsewhen(in.wsel === RF.SEL.DRAM) {
    out.wD := in.from_dram
  }.elsewhen(in.wsel === RF.SEL.IMM) {
    out.wD := in.from_imm
  }.elsewhen(in.wsel === RF.SEL.PC4) {
    out.wD := in.from_pc + 4.U
  }.otherwise {
    out.wD := 0.U
  }

  when(in.we && in.wR =/= 0.U) { // disallow write to x0
    regs(in.wR) := out.wD
  }
}
