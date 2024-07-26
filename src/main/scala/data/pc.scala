package data

import chisel3._
import circt.stage.ChiselStage
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

class PC extends Module {
  val in = IO(new Bundle {
    val din = Input(UInt(32.W))
    val stall = Input(Bool())
  })
  val out = IO(new Bundle { val pc = Output(UInt(32.W)) })

  val output = RegInit(0.U(32.W))
  // wait for one cycle when reset
  val flag = RegInit(0.U(2.W))
  
  when(flag === 0.U) {
    flag := 1.U
  }.otherwise {
    flag := flag
  }

  when(in.stall) {
    output := output
  }.otherwise {
    when(flag === 1.U) {
      output := in.din
    }.otherwise {
      output := 0.U
    }
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

class NPC extends Module {
  val in = IO(Input(new Bundle {
    val pc = UInt(32.W)
    val jump = Bool()
    val pc_jump = UInt(32.W)
  }))

  @public val out = IO(new Bundle {
    val npc = Output(UInt(32.W))
  })
  
  out.npc := Mux(in.jump, in.pc_jump, in.pc + 4.U)
}
