import chisel3._

import data._
import chisel3.experimental.hierarchy.Instantiate
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public



class CPU extends Module {

  val io_irom = IO(new Bundle {
    val inst_addr = Output(UInt(16.W))
    val inst = Input(UInt(32.W))
  })

  val io_bus = IO(new Bundle {
    val addr = Output(UInt(32.W))
    val we = Output(Bool())
    val bus_in = Input(UInt(32.W))
    val bus_out = Output(UInt(32.W))
  })
  
  val trace = IO(Output(new Bundle {
    val have_inst = Bool()
    val pc = UInt(32.W)
    val ena = Bool()
    val wb_reg = UInt(5.W)
    val wb_value = UInt(32.W)
  }))
  
  val i_ctrl = Module(new Control())
  val i_alu = Module(new ALU())
  val i_pc = Module(new PC())
  val i_npc = Module(new NPC())
  val i_rf = Module(new RF())
  val i_sext = Module(new SEXT())
  
  val i_jump = Module(new JumpMux())
  
  val reg_if_id = Module(new REG_IF_ID)
  val reg_id_ex = Module(new REG_ID_EX)
  val reg_ex_mem = Module(new REG_EX_MEM)
  val reg_mem_wb = Module(new REG_MEM_WB)
  
  val data_hazard_detector = Module(new DataHazardDetector)
  val control_hazard_detector = Module(new ControlHazardDectector)

  val rf_mux1 = Module(new RFMux1)
  val rf_mux2 = Module(new RFMux2)
  
  trace.have_inst := reg_mem_wb.out_data.has_inst
  trace.pc := reg_mem_wb.out_data.pc
  trace.ena := reg_mem_wb.out_data.we
  trace.wb_reg := reg_mem_wb.out_data.wR
  trace.wb_value := reg_mem_wb.out_data.wD
  
  // IF
  i_pc.in.din := i_npc.out.npc
  i_pc.in.stall := data_hazard_detector.out.stall_PC
  i_npc.in.pc := i_pc.out.pc
  i_npc.in.jump := i_jump.out.jump
  i_npc.in.pc_jump := i_jump.out.pc_jump
  io_irom.inst_addr := i_pc.out.pc(15,0)

  reg_if_id.in_data.pc := i_pc.out.pc
  reg_if_id.in_data.inst := io_irom.inst
  reg_if_id.signal.stall := data_hazard_detector.out.stall_REG_IF_ID
  reg_if_id.signal.flush := control_hazard_detector.out.flush_REG_IF_ID
  
  // ID
  i_ctrl.in.inst := reg_if_id.out_data.inst
  i_sext.in.din := reg_if_id.out_data.inst(31,7)
  i_sext.in.op := i_ctrl.out.sext_op
  i_rf.in.rR1 := reg_if_id.out_data.inst(19, 15)
  i_rf.in.rR2 := reg_if_id.out_data.inst(24, 20)
  i_rf.in.we := reg_mem_wb.out_data.we
  i_rf.in.wR := reg_mem_wb.out_data.wR
  i_rf.in.wD := reg_mem_wb.out_data.wD
  
  reg_id_ex.in_basic_data.pc := reg_if_id.out_data.pc
  reg_id_ex.in_basic_data.alu_bsel := i_ctrl.out.alu_bsel
  reg_id_ex.in_basic_data.alu_op := i_ctrl.out.alu_op
  reg_id_ex.in_basic_data.branch := i_ctrl.out.branch
  reg_id_ex.in_basic_data.rf_wsel := i_ctrl.out.rf_wsel
  reg_id_ex.in_basic_data.rf_we := i_ctrl.out.rf_we
  reg_id_ex.in_basic_data.ram_we := i_ctrl.out.ram_we
  reg_id_ex.in_basic_data.has_inst := i_ctrl.out.has_inst
  reg_id_ex.in_basic_data.imm := i_sext.out.dout
  reg_id_ex.in_basic_data.wR := reg_if_id.out_data.inst(11, 7)
  
  reg_id_ex.in_reg.rD1 := i_rf.out.rD1
  reg_id_ex.in_reg.rD2 := i_rf.out.rD2
  reg_id_ex.forward.forward_op1 := data_hazard_detector.out.forward_op1
  reg_id_ex.forward.forward_op2 := data_hazard_detector.out.forward_op2
  reg_id_ex.forward.forward_rD1 := data_hazard_detector.out.forward_rD1
  reg_id_ex.forward.forward_rD2 := data_hazard_detector.out.forward_rD2

  reg_id_ex.signal.flush := data_hazard_detector.out.flush_REG_ID_EX || control_hazard_detector.out.flush_REG_ID_EX
  reg_id_ex.signal.stall := false.B
  
  // EX
  i_alu.in.a := reg_id_ex.out_reg.rD1
  i_alu.in.b := reg_id_ex.out_reg.rD2
  i_alu.in.imm := reg_id_ex.out_basic_data.imm
  i_alu.in.sel := reg_id_ex.out_basic_data.alu_bsel
  i_alu.in.alu_op := reg_id_ex.out_basic_data.alu_op
  
  i_jump.in.pc := reg_id_ex.out_basic_data.pc
  i_jump.in.imm := reg_id_ex.out_basic_data.imm
  i_jump.in.branch := reg_id_ex.out_basic_data.branch
  i_jump.in.alu_res := i_alu.out.res
  i_jump.in.bf := i_alu.out.br
  
  rf_mux1.in.from_alu := i_alu.out.res
  rf_mux1.in.from_imm := reg_id_ex.out_basic_data.imm
  rf_mux1.in.from_pc := reg_id_ex.out_basic_data.pc
  rf_mux1.in.wsel := reg_id_ex.out_basic_data.rf_wsel
  
  reg_ex_mem.in_data.ram_we := reg_id_ex.out_basic_data.ram_we
  reg_ex_mem.in_data.pc := reg_id_ex.out_basic_data.pc
  reg_ex_mem.in_data.rD2 := reg_id_ex.out_reg.rD2
  reg_ex_mem.in_data.alu_res := i_alu.out.res
  reg_ex_mem.in_data.has_inst := reg_id_ex.out_basic_data.has_inst
  reg_ex_mem.in_data.rf_wsel := reg_id_ex.out_basic_data.rf_wsel
  reg_ex_mem.in_data.rf_we := reg_id_ex.out_basic_data.rf_we
  reg_ex_mem.in_data.wR := reg_id_ex.out_basic_data.wR
  reg_ex_mem.in_data.wD := rf_mux1.out.wD
  reg_ex_mem.signal.flush := false.B
  reg_ex_mem.signal.stall := false.B
  
  // MEM
  rf_mux2.in.wD := reg_ex_mem.out_data.wD
  rf_mux2.in.from_dram := io_bus.bus_in
  rf_mux2.in.wsel := reg_ex_mem.out_data.rf_wsel
  
  reg_mem_wb.in_data.has_inst := reg_ex_mem.out_data.has_inst
  reg_mem_wb.in_data.we := reg_ex_mem.out_data.rf_we
  reg_mem_wb.in_data.wR := reg_ex_mem.out_data.wR
  reg_mem_wb.in_data.wD := rf_mux2.out.wD
  reg_mem_wb.in_data.pc := reg_ex_mem.out_data.pc
  reg_mem_wb.signal.flush := false.B
  reg_mem_wb.signal.stall := false.B
  
  // Hazard
  data_hazard_detector.in.ID.readR1 := i_ctrl.out.ID_read1
  data_hazard_detector.in.ID.readR2 := i_ctrl.out.ID_read2
  data_hazard_detector.in.ID.rR1 := reg_if_id.out_data.inst(19, 15)
  data_hazard_detector.in.ID.rR2 := reg_if_id.out_data.inst(24, 20)
  data_hazard_detector.in.EX.rf_wen := reg_id_ex.out_basic_data.rf_we
  data_hazard_detector.in.EX.rf_wsel := reg_id_ex.out_basic_data.rf_wsel
  data_hazard_detector.in.EX.wR := reg_id_ex.out_basic_data.wR
  data_hazard_detector.in.EX.wD := rf_mux1.out.wD
  data_hazard_detector.in.MEM.rf_wen := reg_ex_mem.out_data.rf_we
  data_hazard_detector.in.MEM.wR := reg_ex_mem.out_data.wR
  data_hazard_detector.in.MEM.wD := rf_mux2.out.wD
  data_hazard_detector.in.WB.rf_wen := reg_mem_wb.out_data.we
  data_hazard_detector.in.WB.wR := reg_mem_wb.out_data.wR
  data_hazard_detector.in.WB.wD := reg_mem_wb.out_data.wD
  
  control_hazard_detector.in.is_control_hazard := i_jump.out.jump
  
  io_bus.addr := reg_ex_mem.out_data.alu_res
  io_bus.bus_out := reg_ex_mem.out_data.rD2
  io_bus.we := reg_ex_mem.out_data.ram_we

}
