package data

import chisel3._
import chisel3.util.Cat
import chisel3.util.Fill
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

object SEXT {
  object OP {

    val I = 0.U
    val S = 1.U
    val B = 2.U
    val U = 3.U
    val J = 4.U
    val SHIFT = 5.U
  }
}

@instantiable
class SEXT extends Module {
  @public val in = IO(new Bundle {
    val din = Input(UInt(25.W))
    val op = Input(UInt(3.W))
  })
  @public val out = IO(new Bundle {
    val dout = Output(UInt(32.W))
  })
/*
 *             `EXT_I: ext = din[24]? {20'hFFFFF,din[24:13]} : {20'h00000,din[24:13]};       
            `EXT_S: ext = din[24]? {20'hFFFFF,din[24:18],din[4:0]} : {20'h00000,din[24:18],din[4:0]};      
            `EXT_B: ext = din[24]? {20'hFFFFF,din[0],din[23:18],din[4:1],1'b0} : {20'h00000,din[0],din[23:18],din[4:1],1'b0};
            `EXT_U: ext = {din[24:5],12'h000};    
            `EXT_J: ext = din[24]? {12'hFFF,din[12:5],din[13],din[23:14],1'b0} : {12'h000,din[12:5],din[13],din[23:14],1'b0};
            `EXT_SHIFT: ext = {27'd0,din[17:13]};    
            default: ext =32'h0000_0000;
 */
  when(in.op === SEXT.OP.I) {
    out.dout := Cat(Fill(20, in.din(24)), in.din(24, 13))
  }.elsewhen(in.op === SEXT.OP.S) {
    out.dout := Cat(Fill(20, in.din(24)), in.din(24, 18), in.din(4, 0))
  }.elsewhen(in.op === SEXT.OP.B) {
    out.dout := Cat(Fill(20, in.din(24)), in.din(0), in.din(23, 18), in.din(4, 1), 0.U)
  }.elsewhen(in.op === SEXT.OP.U) {
    out.dout := Cat(in.din(24, 5), Fill(12, 0.U))
  }.elsewhen(in.op === SEXT.OP.J) {
    out.dout := Cat(Fill(12, in.din(24)), in.din(12, 5), in.din(13), in.din(23, 14), 0.U)
  }.elsewhen(in.op === SEXT.OP.SHIFT) {
    out.dout := Cat(Fill(27, 0.U), in.din(17, 13))
  }.otherwise {
    out.dout := Fill(32, 0.U)
  }
}
