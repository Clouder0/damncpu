package data

import chisel3._
import chisel3.experimental.hierarchy.public

object JUMPSEL {
  val PC4 = 0.U
  val B = 1.U
  val JAL = 2.U
  val JALR = 3.U
}

class JumpMux extends Module {
  @public val in = IO(Input(new Bundle {
    val branch = UInt(2.W)
    val bf = Bool()
    val pc = UInt(32.W)
    val imm = UInt(32.W)
    val alu_res = UInt(32.W)
  }))
  val out = IO(Output(new Bundle {
    val jump = Bool()
    val pc_jump = UInt(32.W)
  }))
  val b_jump = (in.branch === JUMPSEL.B) && in.bf
  val jal = in.branch === JUMPSEL.JAL
  val jalr = in.branch === JUMPSEL.JALR

  out.jump := b_jump || jal || jalr
  out.pc_jump := Mux(in.branch === JUMPSEL.PC4, in.pc + 4.U, Mux(jalr, in.alu_res, in.pc + in.imm))
}

class RFMux1 extends Module {
  @public val in = IO(Input(new Bundle {
    val wsel = UInt(2.W)
    val from_alu = UInt(32.W)
    val from_imm = UInt(32.W)
    val from_pc = UInt(32.W)
  }))
  val out = IO(Output(new Bundle {
    val wD = UInt(32.W)
  }))
  
  when(in.wsel === RF.SEL.ALU) {
    out.wD := in.from_alu
  }.elsewhen(in.wsel === RF.SEL.IMM) {
    out.wD := in.from_imm
  }.elsewhen(in.wsel === RF.SEL.PC4) {
    out.wD := in.from_pc + 4.U
  }.otherwise {
    out.wD := 0.U
  }
}

class RFMux2 extends Module {
  @public val in = IO(Input(new Bundle {
    val wsel = UInt(2.W)
    val wD = UInt(32.W)
    val from_dram = UInt(32.W)
  }))
  val out = IO(Output(new Bundle {
    val wD = UInt(32.W)
  }))

  out.wD := Mux(in.wsel === RF.SEL.DRAM, in.from_dram, in.wD)
}
