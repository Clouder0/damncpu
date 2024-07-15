package data

import chisel3._

object ALU {
  object OP {
    val ADD = 0.U
    val SUB = 1.U
    val AND = 2.U
    val OR = 3.U
    val XOR = 4.U
    val SLL = 5.U
    val SRL = 6.U
    val SRA = 7.U
    val BEQ = 8.U
    val BNE = 9.U
    val BLT = 10.U
    val BGE = 11.U

  }
}

class ALU extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(32.W))
    val b = Input(UInt(32.W))
    val imm = Input(UInt(32.W))
    val sel = Input(Bool()) // select b or imm
    val alu_op = Input(UInt(4.W))
    val res = Output(UInt(32.W))
    val bf = Output(Bool())
  })

  val b_ = Mux(io.sel, io.imm, io.b)
  val low_bits = b_(4, 0)

  when(io.alu_op === ALU.OP.ADD) {
    io.res := io.a + b_
    io.bf := false.B
  }.elsewhen(io.alu_op === ALU.OP.SUB) {
    io.res := io.a - b_
    io.bf := false.B
  }.elsewhen(io.alu_op === ALU.OP.AND) {
    io.res := io.a & b_
    io.bf := false.B
  }.elsewhen(io.alu_op === ALU.OP.OR) {
    io.res := io.a | b_
    io.bf := false.B
  }.elsewhen(io.alu_op === ALU.OP.XOR) {
    io.res := io.a ^ b_
    io.bf := false.B
  }.elsewhen(io.alu_op === ALU.OP.SLL) {
    io.res := io.a << low_bits
    io.bf := false.B
  }.elsewhen(io.alu_op === ALU.OP.SRL) {
    io.res := io.a >> low_bits
    io.bf := false.B
  }.elsewhen(io.alu_op === ALU.OP.SRA) {
    io.res := (io.a.asSInt >> low_bits).asUInt
    io.bf := false.B
  }.elsewhen(io.alu_op === ALU.OP.BEQ) {
    io.res := 0.U
    io.bf := io.a === b_
  }.elsewhen(io.alu_op === ALU.OP.BNE) {
    io.res := 0.U
    io.bf := io.a =/= b_
  }.elsewhen(io.alu_op === ALU.OP.BLT) {
    io.res := 0.U
    io.bf := io.a.asSInt < b_.asSInt
  }.elsewhen(io.alu_op === ALU.OP.BGE) {
    io.res := 0.U
    io.bf := io.a.asSInt >= b_.asSInt
  }.otherwise {
    io.res := 0.U
    io.bf := false.B
  }
}
