import chisel3._
import data.RF

class ControlHazardDectector extends Module {
  val in = IO(Input(new Bundle {
    val is_control_hazard = Bool()
  }))

  val out = IO(Output(new Bundle {
    val flush_REG_IF_ID = Bool()
    val flush_REG_ID_EX = Bool()
  }))

  out.flush_REG_IF_ID := in.is_control_hazard
  out.flush_REG_ID_EX := in.is_control_hazard
}

class DataHazardDetector extends Module {
  val in = IO(Input(new Bundle {
    val ID = new Bundle {
      val readR1 = Bool()
      val readR2 = Bool()
      val rR1 = UInt(5.W)
      val rR2 = UInt(5.W)
    }
    val EX = new Bundle {
      val rf_wsel = UInt(2.W)
      val rf_wen = Bool()
      val wR = UInt(5.W)
      val wD = UInt(32.W)
    }
    val MEM = new Bundle {
      val rf_wen = Bool()
      val wR = UInt(5.W)
      val wD = UInt(32.W)
    }
    val WB = new Bundle {
      val rf_wen = Bool()
      val wR = UInt(5.W)
      val wD = UInt(32.W)
    }
  }))

  val out = IO(Output(new Bundle {
    val stall_REG_IF_ID = Bool()
    val flush_REG_ID_EX = Bool()
    val stall_PC = Bool()
    val forward_op1 = Bool()
    val forward_op2 = Bool()
    val forward_rD1 = UInt(32.W)
    val forward_rD2 = UInt(32.W)
  }))

  // read after write for EX
  val RAW_EX_rR1 =
    in.EX.rf_wen && in.ID.readR1 && in.ID.rR1 === in.EX.wR && !(in.EX.wR === 0.U)
  val RAW_EX_rR2 =
    in.EX.rf_wen && in.ID.readR2 && in.ID.rR2 === in.EX.wR && !(in.EX.wR === 0.U)

  // read after write for MEM
  val RAW_MEM_rR1 =
    in.MEM.rf_wen && in.ID.readR1 && in.ID.rR1 === in.MEM.wR && !(in.MEM.wR === 0.U)
  val RAW_MEM_rR2 =
    in.MEM.rf_wen && in.ID.readR2 && in.ID.rR2 === in.MEM.wR && !(in.MEM.wR === 0.U)

  // read after write for WB
  val RAW_WB_rR1 =
    in.WB.rf_wen && in.ID.readR1 && in.ID.rR1 === in.WB.wR && !(in.WB.wR === 0.U)
  val RAW_WB_rR2 =
    in.WB.rf_wen && in.ID.readR2 && in.ID.rR2 === in.WB.wR && !(in.WB.wR === 0.U)

  // load-use hazard
  val load_use = (RAW_EX_rR1 || RAW_EX_rR2) && in.EX.rf_wsel === RF.SEL.DRAM

  out.forward_op1 := RAW_EX_rR1 || RAW_MEM_rR1 || RAW_WB_rR1
  out.forward_op2 := RAW_EX_rR2 || RAW_MEM_rR2 || RAW_WB_rR2
  out.forward_rD1 := Mux(
    RAW_EX_rR1,
    in.EX.wD,
    Mux(RAW_MEM_rR1, in.MEM.wD, in.WB.wD)
  )
  out.forward_rD2 := Mux(
    RAW_EX_rR2,
    in.EX.wD,
    Mux(RAW_MEM_rR2, in.MEM.wD, in.WB.wD)
  )

  // load_use: install(PC & IF/ID), flush(ID/EX)
  out.stall_PC := load_use
  out.stall_REG_IF_ID := load_use
  out.flush_REG_ID_EX := load_use

}
