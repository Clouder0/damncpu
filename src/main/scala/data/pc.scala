package data

import chisel3._
import circt.stage.ChiselStage

class PC extends Module {
  val io = IO(new Bundle {
    val din = Input(UInt(32.W))
    val pc = Output(UInt(32.W))
  })
  val output = RegInit(0.U(32.W))
  output := io.din
  io.pc := output
}
object NPC {
  object OP {
    val PC4 = 0.U
    val BR = 1.U
    val J = 2.U
    val JR = 3.U
  }
}
class NPC extends Module {

  val io = IO(new Bundle {
    val pc = Input(UInt(32.W))
    val offset = Input(UInt(32.W))
    val br = Input(Bool())
    val op = Input(UInt(2.W))
    val alu_res = Input(UInt(32.W))
    val npc = Output(UInt(32.W))
  })
  when(io.op === NPC.OP.PC4) {
    io.npc := io.pc + 4.U // PC := PC + 4
  }.elsewhen(io.op === NPC.OP.BR) {
    // branch
    when(io.br) {
      io.npc := io.pc + io.offset
    }.otherwise {
      io.npc := io.pc + 4.U
    }
  }.elsewhen(io.op === NPC.OP.J) {
    // jump
    io.npc := io.pc + io.offset
  }.otherwise {
    // alu result
    io.npc := io.alu_res
  }
}

object PC extends App {
  ChiselStage.emitSystemVerilogFile(
    new PC,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}
