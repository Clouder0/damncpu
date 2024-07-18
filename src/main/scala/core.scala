import chisel3._

import data._
import chisel3.experimental.hierarchy.Instantiate
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

@instantiable
class CPU extends Module {
    
    @public val io_irom = IO(new Bundle {
        val inst_addr = Output(UInt(16.W))
        val inst = Input(UInt(32.W))
    })
    
    @public val io_dram = IO(new Bundle {
        val alu_res = Output(UInt(32.W))
        val rD2 = Output(UInt(32.W))
        val dram_rdata = Input(UInt(32.W))
        val we = Output(Bool())
    })
    
    
    @public val i_ctrl = Instantiate(new Control())
    @public val i_alu = Instantiate(new ALU())
    @public val i_pc = Instantiate(new PC())
    @public val i_npc = Instantiate(new NPC())
    @public val i_rf = Instantiate(new RF())
    @public val i_sext = Instantiate(new SEXT())
    
    i_ctrl.in.inst := io_irom.inst
    i_ctrl.in.br := i_alu.out.br
    
    i_pc.in.din := i_npc.out.npc

    i_npc.in.pc := i_pc.out.pc
    i_npc.in.offset := i_sext.out.dout
    i_npc.in.alu_res := i_alu.out.res
    i_npc.in.op := i_ctrl.out.npc_op
    
    i_rf.in.from_alu := i_alu.out.res
    i_rf.in.from_dram := io_dram.dram_rdata
    i_rf.in.from_imm := i_sext.out.dout
    i_rf.in.from_pc := i_pc.out.pc
    
    i_rf.in.we := i_ctrl.out.rf_we
    i_rf.in.wsel := i_ctrl.out.rf_wsel
    i_rf.in.rR1 := io_irom.inst(19,15)
    i_rf.in.rR2 := io_irom.inst(24,20)
    i_rf.in.wR := io_irom.inst(11,7)
    
    i_alu.in.alu_op := i_ctrl.out.alu_op
    i_alu.in.a := i_rf.out.rD1
    i_alu.in.b := i_rf.out.rD2
    i_alu.in.imm := i_sext.out.dout
    i_alu.in.sel := i_ctrl.out.alu_bsel
    
    i_sext.in.din := io_irom.inst(31,7)
    i_sext.in.op := i_ctrl.out.sext_op
    
    io_irom.inst_addr := i_pc.out.pc(15,0)
    io_dram.we := i_ctrl.out.ram_we
    io_dram.alu_res := i_alu.out.res
    io_dram.rD2 := i_rf.out.rD2
}
