import chisel3._

import data._

class control extends Module {
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

  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val bf = Input(Bool())
    val npc_op = Output(UInt(2.W))
    val alu_op = Output(UInt(4.W))
    val alu_bsel = Output(Bool())
    val sext_op = Output(UInt(3.W))
    val rf_we = Output(Bool())
    val rf_wR = Output(UInt(5.W))
    val ram_we = Output(Bool())
  })

  val opcode = io.inst(6, 0)
  val func3 = io.inst(14, 12)
  val func7 = io.inst(31, 25)

  when(opcode === OP.R) {
    io.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.I) {
    io.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.LW) {
    io.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.JALR) {
    io.npc_op := data.NPC.OP.JR
  }.elsewhen(opcode === OP.SW) {
    io.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.B) {
    io.npc_op := Mux(io.bf, data.NPC.OP.BR, data.NPC.OP.PC4)
  }.elsewhen(opcode === OP.U) {
    io.npc_op := data.NPC.OP.PC4
  }.elsewhen(opcode === OP.J) {
    io.npc_op := data.NPC.OP.J
  }.otherwise {
    io.npc_op := data.NPC.OP.PC4
  }

  when(opcode === OP.R || opcode === OP.I) {
    when(func3 === "b000".U) {
      when(opcode === OP.R) {
        io.alu_op := Mux(func7 === "b0000000".U, ALU.OP.ADD, ALU.OP.SUB)
      }.otherwise {
        io.alu_op := ALU.OP.ADD
      }
    }.elsewhen(func3 === "b111".U) {
      io.alu_op := ALU.OP.AND
    }.elsewhen(func3 === "b110".U) {
      io.alu_op := ALU.OP.OR
    }.elsewhen(func3 === "b100".U) {
      io.alu_op := ALU.OP.XOR
    }.elsewhen(func3 === "b001".U) {
      io.alu_op := ALU.OP.SLL
    }.elsewhen(func3 === "b101".U) {
      io.alu_op := Mux(func7 === "b0000000".U, ALU.OP.SRL, ALU.OP.SRA)
    }.otherwise {
      io.alu_op := ALU.OP.ADD
    }
  }.elsewhen(opcode === OP.LW) {
    io.alu_op := ALU.OP.ADD
  }.elsewhen(opcode === OP.JALR) {
    io.alu_op := ALU.OP.ADD
  }.elsewhen(opcode === OP.SW) {
    io.alu_op := ALU.OP.ADD
  }.elsewhen(opcode === OP.B) {
    when(func3 === "b000".U) {
      io.alu_op := ALU.OP.BEQ
    }.elsewhen(func3 === "b001".U) {
      io.alu_op := ALU.OP.BNE
    }.elsewhen(func3 === "b100".U) {
      io.alu_op := ALU.OP.BLT
    }.elsewhen(func3 === "b101".U) {
      io.alu_op := ALU.OP.BGE
    }.otherwise {
      io.alu_op := ALU.OP.BEQ
    }
  }.otherwise {
    io.alu_op := ALU.OP.ADD
  }

  when(opcode === OP.R) {
    io.alu_bsel := false.B
  }.elsewhen(opcode === OP.I) {
    io.alu_bsel := true.B
  }.elsewhen(opcode === OP.LW) {
    io.alu_bsel := true.B
  }.elsewhen(opcode === OP.JALR) {
    io.alu_bsel := true.B
  }.elsewhen(opcode === OP.SW) {
    io.alu_bsel := true.B
  }.elsewhen(opcode === OP.B) {
    io.alu_bsel := true.B
  }.elsewhen(opcode === OP.U) {
    io.alu_bsel := false.B
  }.elsewhen(opcode === OP.J) {
    io.alu_bsel := false.B
  }.otherwise {
    io.alu_bsel := false.B
  }

  when(opcode === OP.I) {
    when(func3 === "b001".U || func3 === "b101".U) {
      io.sext_op := SEXT.OP.SHIFT
    }.otherwise {
      io.sext_op := SEXT.OP.I
    }
  }.elsewhen(opcode === OP.LW) {
    io.sext_op := SEXT.OP.I
  }.elsewhen(opcode === OP.JALR) {
    io.sext_op := SEXT.OP.I
  }.elsewhen(opcode === OP.SW) {
    io.sext_op := SEXT.OP.S
  }.elsewhen(opcode === OP.B) {
    io.sext_op := SEXT.OP.B
  }.elsewhen(opcode === OP.U) {
    io.sext_op := SEXT.OP.U
  }.elsewhen(opcode === OP.J) {
    io.sext_op := SEXT.OP.J
  }.otherwise {
    io.sext_op := SEXT.OP.I
  }

  when(opcode === OP.SW || opcode === OP.B) {
    io.rf_we := false.B
  }.otherwise {
    io.rf_we := true.B
  }

  when(opcode === OP.R || opcode === OP.I) {
    io.rf_wR := RF.SEL.ALU
  }.elsewhen(opcode === OP.LW) {
    io.rf_wR := RF.SEL.DRAM
  }.elsewhen(opcode === OP.JALR) {
    io.rf_wR := RF.SEL.PC
  }.elsewhen(opcode === OP.J) {
    io.rf_wR := RF.SEL.PC
  }.elsewhen(opcode === OP.U) {
    io.rf_wR := RF.SEL.IMM
  }.otherwise {
    io.rf_wR := RF.SEL.PC
  }

  when(opcode === OP.SW) {
    io.ram_we := true.B
  }.otherwise {
    io.ram_we := false.B
  }

}
