
import chisel3._
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public
import chisel3.experimental.hierarchy.Instantiate

class PipelineReg[T <: Data](gen: T) extends Module {
  val in = IO(Input(gen))
  val out = IO(Output(gen))
  val signal = IO(new Bundle {
    val stall = Input(Bool())
    val flush = Input(Bool())
  })

  val zero = 0.U.asTypeOf(gen)
  val reg = RegInit(zero)

  when(signal.flush) {
    reg := zero
  }.elsewhen(signal.stall) {
    reg := reg
  }.otherwise {
    reg := in
  }

  out := reg
}

class IFIDBundle extends Bundle {
  val pc = UInt(32.W)
  val inst = UInt(32.W)
}
class IDEXBundle extends Bundle {
  val wR = UInt(5.W)
  val pc = UInt(32.W)
  val imm = UInt(32.W)
  val has_inst = Bool()

  val rf_wsel = UInt(2.W)
  val rf_we = Bool()
  val branch = UInt(2.W)
  val ram_we = Bool()
  val alu_op = UInt(4.W)
  val alu_bsel = Bool()
}

class REG_IF_ID extends Module {
  val signal = IO(Input(new Bundle {
    val stall = Bool()
    val flush = Bool()

  }))
  val in_data = IO(Input(new IFIDBundle))
  val out_data = IO(Output(new IFIDBundle))

  val reg = Module(new PipelineReg(new IFIDBundle))

  reg.in := in_data
  reg.signal := signal
  out_data := reg.out
}

class REG_ID_EX extends Module {
  val signal = IO(new Bundle {
    val stall = Input(Bool())
    val flush = Input(Bool())
  })
  val forward = IO(Input(new Bundle {
    val forward_op1 = Bool()
    val forward_op2 = Bool()
    val forward_rD1 = UInt(32.W)
    val forward_rD2 = UInt(32.W)
  }))
  val in_basic_data = IO(Input(new IDEXBundle))
  val out_basic_data = IO(Output(new IDEXBundle))
  val in_reg = IO(Input(new Bundle {
    val rD1 = UInt(32.W)
    val rD2 = UInt(32.W)
  }))
  val out_reg = IO(Output(new Bundle {
    val rD1 = UInt(32.W)
    val rD2 = UInt(32.W)
  }))

  val reg = Module(new PipelineReg(new IDEXBundle))
  val r_reg = Module(new PipelineReg(new Bundle {
    val rD1 = UInt(32.W)
    val rD2 = UInt(32.W)
  }))
  reg.in := in_basic_data
  reg.signal := signal
  
  r_reg.in.rD1 := Mux(forward.forward_op1, forward.forward_rD1, in_reg.rD1)
  r_reg.in.rD2 := Mux(forward.forward_op2, forward.forward_rD2, in_reg.rD2)
  r_reg.signal := signal

  out_basic_data := reg.out
  out_reg := r_reg.out
  

}

class EXMEMBundle extends Bundle {
  val wR = UInt(5.W)
  val wD = UInt(32.W)
  val pc = UInt(32.W)
  val rD2 = UInt(32.W)
  val alu_res = UInt(32.W)
  val has_inst = Bool()
  val rf_wsel = UInt(2.W)
  val rf_we = Bool()
  val ram_we = Bool()
}

class REG_EX_MEM extends Module {
  val signal = IO(new Bundle {
    val stall = Input(Bool())
    val flush = Input(Bool())
  })
  val in_data = IO(Input(new EXMEMBundle))
  val out_data = IO(Output(new EXMEMBundle))

  val reg = Module(new PipelineReg(new EXMEMBundle))
  reg.in := in_data
  reg.signal := signal
  out_data := reg.out
}

class MEMWBBundle extends Bundle {
  val wR = UInt(5.W)
  val wD = UInt(32.W)
  val pc = UInt(32.W)
  val we = Bool()
  val has_inst = Bool()
}

class REG_MEM_WB extends Module {
  val signal = IO(new Bundle {
    val stall = Input(Bool())
    val flush = Input(Bool())
  })
  val in_data = IO(Input(new MEMWBBundle))
  val out_data = IO(Output(new MEMWBBundle))

  val reg = Module(new PipelineReg(new MEMWBBundle))
  reg.in := in_data
  reg.signal := signal
  out_data := reg.out
}