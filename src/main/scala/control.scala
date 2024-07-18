import chisel3._

import data._
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

@instantiable
class Control extends Module {
  object OP {
    val R = "b011001".U
    val I = "b0010011".U
    val LW = "b0000011".U
    val JALR = "b1100111".U
    val SW = "b0100011".U
    val B = "b1100011".U
    val U = "b0110111".U
    val J = "b110111".U
  }

  @public val in = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val br = Input(Bool())
  })

  @public val out = IO(new Bundle {
    val npc_op = Output(UInt(2.W))
    val alu_op = Output(UInt(4.W))
    val alu_bsel = Output(Bool())
    val sext_op = Output(UInt(3.W))
    val rf_we = Output(Bool())
    val rf_wsel = Output(UInt(2.W))
    val ram_we = Output(Bool())
  })

  val opcode = in.inst(6, 0)
  val func3 = in.inst(14, 12)
  val func7 = in.inst(31, 25)

  when(opcode === OP.R) {
    out.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.I) {
    out.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.LW) {
    out.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.JALR) {
    out.npc_op := data.NPC.OP.JR
  }.elsewhen(opcode === OP.SW) {
    out.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.B) {
    out.npc_op := Mux(in.br, data.NPC.OP.BR, data.NPC.OP.PC4)
  }.elsewhen(opcode === OP.U) {
    out.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.J) {
    out.npc_op := data.NPC.OP.J
  }.otherwise {
    out.npc_op := data.NPC.OP.PC4
  }

  when(opcode === OP.R || opcode === OP.I) {
    when(func3 === "b000".U) {
      when(opcode === OP.R) {
        out.alu_op := Mux(func7 === "b0000000".U, ALU.OP.ADD, ALU.OP.SUB)
      }.otherwise {
        out.alu_op := ALU.OP.ADD
      }
    }.elsewhen(func3 === "b111".U) {
      out.alu_op := ALU.OP.AND
    }.elsewhen(func3 === "b110".U) {
      out.alu_op := ALU.OP.OR
    }.elsewhen(func3 === "b100".U) {
      out.alu_op := ALU.OP.XOR
    }.elsewhen(func3 === "b001".U) {
      out.alu_op := ALU.OP.SLL
    }.elsewhen(func3 === "b101".U) {
      out.alu_op := Mux(func7 === "b0000000".U, ALU.OP.SRL, ALU.OP.SRA)
    }.otherwise {
      out.alu_op := ALU.OP.ADD
    }
  }.elsewhen(opcode === OP.LW) {
    out.alu_op := ALU.OP.ADD
  }.elsewhen(opcode === OP.JALR) {
    out.alu_op := ALU.OP.ADD
  }.elsewhen(opcode === OP.SW) {
    out.alu_op := ALU.OP.ADD
  }.elsewhen(opcode === OP.B) {
    when(func3 === "b000".U) {
      out.alu_op := ALU.OP.BEQ
    }.elsewhen(func3 === "b001".U) {
      out.alu_op := ALU.OP.BNE
    }.elsewhen(func3 === "b100".U) {
      out.alu_op := ALU.OP.BLT
    }.elsewhen(func3 === "b101".U) {
      out.alu_op := ALU.OP.BGE
    }.otherwise {
      out.alu_op := ALU.OP.BEQ
    }
  }.otherwise {
    out.alu_op := ALU.OP.ADD
  }

  when(opcode === OP.R) {
    out.alu_bsel := false.B
  }.elsewhen(opcode === OP.I) {
    out.alu_bsel := true.B
  }.elsewhen(opcode === OP.LW) {
    out.alu_bsel := true.B
  }.elsewhen(opcode === OP.JALR) {
    out.alu_bsel := true.B
  }.elsewhen(opcode === OP.SW) {
    out.alu_bsel := true.B
  }.elsewhen(opcode === OP.B) {
    out.alu_bsel := true.B
  }.elsewhen(opcode === OP.U) {
    out.alu_bsel := false.B
  }.elsewhen(opcode === OP.J) {
    out.alu_bsel := false.B
  }.otherwise {
    out.alu_bsel := false.B
  }

  when(opcode === OP.I) {
    when(func3 === "b001".U || func3 === "b101".U) {
      out.sext_op := SEXT.OP.SHIFT
    }.otherwise {
      out.sext_op := SEXT.OP.I
    }
  }.elsewhen(opcode === OP.LW) {
    out.sext_op := SEXT.OP.I
  }.elsewhen(opcode === OP.JALR) {
    out.sext_op := SEXT.OP.I
  }.elsewhen(opcode === OP.SW) {
    out.sext_op := SEXT.OP.S
  }.elsewhen(opcode === OP.B) {
    out.sext_op := SEXT.OP.B
  }.elsewhen(opcode === OP.U) {
    out.sext_op := SEXT.OP.U
  }.elsewhen(opcode === OP.J) {
    out.sext_op := SEXT.OP.J
  }.otherwise {
    out.sext_op := SEXT.OP.I
  }

  when(opcode === OP.SW || opcode === OP.B) {
    out.rf_we := false.B
  }.otherwise {
    out.rf_we := true.B
  }

  when(opcode === OP.R || opcode === OP.I) {
    out.rf_wsel := RF.SEL.ALU
  }.elsewhen(opcode === OP.LW) {
    out.rf_wsel := RF.SEL.DRAM
  }.elsewhen(opcode === OP.JALR) {
    out.rf_wsel := RF.SEL.PC
  }.elsewhen(opcode === OP.J) {
    out.rf_wsel := RF.SEL.PC
  }.elsewhen(opcode === OP.U) {
    out.rf_wsel := RF.SEL.IMM
  }.otherwise {
    out.rf_wsel := RF.SEL.PC
  }

  when(opcode === OP.SW) {
    out.ram_we := true.B
  }.otherwise {
    out.ram_we := false.B
  }

}
