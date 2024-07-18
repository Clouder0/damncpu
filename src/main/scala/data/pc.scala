package data

import chisel3._
import circt.stage.ChiselStage
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

@instantiable
class PC extends Module {
  @public val in = IO(new Bundle {
    val din = Input(UInt(32.W))
  })
  @public val out = IO(new Bundle { val pc = Output(UInt(32.W)) })
  val output = RegInit(0.U(32.W))
  val flag =  RegInit(false.B)
  when(flag) {
    output := in.din
  }.otherwise {
    flag := true.B
  }
  out.pc := output
}
object NPC {
  object OP {
    val PC4 = 0.U
    val BR = 1.U
    val J = 2.U
    val JR = 3.U
  }
}

@instantiable
class NPC extends Module {

  @public val in = IO(new Bundle {
    val pc = Input(UInt(32.W))
    val offset = Input(UInt(32.W))
    val op = Input(UInt(2.W))
    val alu_res = Input(UInt(32.W))
  })

  @public val out = IO(new Bundle {
    val npc = Output(UInt(32.W))
  })

  when(in.op === NPC.OP.PC4) {
    out.npc := in.pc + 4.U // PC := PC + 4
  }.elsewhen(in.op === NPC.OP.BR) {
    // branch
    out.npc := in.pc + in.offset
  }.elsewhen(in.op === NPC.OP.J) {
    // jump
    out.npc := in.pc + in.offset
  }.otherwise {
    // alu result
    out.npc := in.alu_res
  }
}
