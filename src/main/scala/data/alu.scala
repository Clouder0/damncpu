package data

import chisel3._
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

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
  val in = IO(new Bundle {
    val a = Input(UInt(32.W))
    val b = Input(UInt(32.W))
    val imm = Input(UInt(32.W))
    val sel = Input(Bool()) // select b or imm
    val alu_op = Input(UInt(4.W))
  })

  val out = IO(new Bundle {
    val res = Output(UInt(32.W))
    val br = Output(Bool())
  })

  val b_ = Mux(in.sel, in.imm, in.b)
  val low_bits = b_(4, 0)

  when(in.alu_op === ALU.OP.ADD) {
    out.res := in.a + b_
    out.br := false.B
  }.elsewhen(in.alu_op === ALU.OP.SUB) {
    out.res := in.a - b_
    out.br := false.B
  }.elsewhen(in.alu_op === ALU.OP.AND) {
    out.res := in.a & b_
    out.br := false.B
  }.elsewhen(in.alu_op === ALU.OP.OR) {
    out.res := in.a | b_
    out.br := false.B
  }.elsewhen(in.alu_op === ALU.OP.XOR) {
    out.res := in.a ^ b_
    out.br := false.B
  }.elsewhen(in.alu_op === ALU.OP.SLL) {
    out.res := in.a << low_bits
    out.br := false.B
  }.elsewhen(in.alu_op === ALU.OP.SRL) {
    out.res := in.a >> low_bits
    out.br := false.B
  }.elsewhen(in.alu_op === ALU.OP.SRA) {
    out.res := (in.a.asSInt >> low_bits).asUInt
    out.br := false.B
  }.elsewhen(in.alu_op === ALU.OP.BEQ) {
    out.res := 0.U
    out.br := in.a === b_
  }.elsewhen(in.alu_op === ALU.OP.BNE) {
    out.res := 0.U
    out.br := in.a =/= b_
  }.elsewhen(in.alu_op === ALU.OP.BLT) {
    out.res := 0.U
    out.br := in.a.asSInt < b_.asSInt
  }.elsewhen(in.alu_op === ALU.OP.BGE) {
    out.res := 0.U
    out.br := in.a.asSInt >= b_.asSInt
  }.otherwise {
    out.res := 0.U
    out.br := false.B
  }
}
