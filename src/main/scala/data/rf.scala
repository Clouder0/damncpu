package data

import chisel3._

object RF {
  object SEL {
    val ALU = 0.U
    val DRAM = 1.U
    val IMM = 2.U
    val PC = 3.U

  }
}
class RF extends Module {
  val io = IO(new Bundle {
    val rR1 = Input(UInt(5.W))
    val rR2 = Input(UInt(5.W))
    val wR = Input(UInt(5.W))
    val we = Input(Bool())
    val wsel = Input(UInt(2.W))
    val from_alu = Input(UInt(32.W))
    val from_dram = Input(UInt(32.W))
    val from_imm = Input(UInt(32.W))
    val from_pc = Input(UInt(32.W))
    val rD1 = Output(UInt(32.W))
    val rD2 = Output(UInt(32.W))
  })

  val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  io.rD1 := regs(io.rR1)
  io.rD2 := regs(io.rR2)

  when(io.we && io.wR =/= 0.U) { // disallow write to x0
    when(io.wsel === RF.SEL.ALU) {
      regs(io.wR) := io.from_alu
    }.elsewhen(io.wsel === RF.SEL.DRAM) {
      regs(io.wR) := io.from_dram
    }.elsewhen(io.wsel === RF.SEL.IMM) {
      regs(io.wR) := io.from_imm
    }.elsewhen(io.wsel === RF.SEL.PC) {
      regs(io.wR) := io.from_pc
    }.otherwise {
      regs(io.wR) := 0.U
    }
  }
}
