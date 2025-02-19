import chisel3._
import data._
import circt.stage.ChiselStage
import chisel3.experimental.hierarchy.Instantiate
import mock.DRAM
import chisel3.stage.ChiselGeneratorAnnotation
import misc.Switch
import misc.LEDLoopDisplay

class MockCPU extends Module {
  val core = Instantiate(new CPU)
  val dram = Instantiate(new DRAM)
  val bridge = Instantiate(new Bridge)
  val in = IO(new Bundle {
    val inst = Input(UInt(32.W))
  })
  val out = IO(new Bundle {
    val inst_addr = Output(UInt(16.W))
    val alu_res = Output(UInt(32.W))
  })
  core.io_irom.inst := in.inst
  out.inst_addr := core.io_irom.inst_addr

  bridge.cpu.addr := core.io_bus.addr
  bridge.cpu.data_from_cpu := core.io_bus.bus_out
  bridge.cpu.we := core.io_bus.we
  core.io_bus.bus_in := bridge.cpu.data_to_cpu

  dram.in.addr := bridge.dram.addr
  dram.in.din := bridge.dram.data_to_dram
  bridge.dram.data_from_dram := dram.out.dout
  dram.in.we := bridge.dram.we

  out.alu_res := core.io_bus.addr
}

object Main extends App {
  ChiselStage.emitSystemVerilogFile(
    new CPU,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "--verilog")
  )
  ChiselStage.emitSystemVerilogFile(
    new Bridge,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "--verilog")
  )
  ChiselStage.emitSystemVerilogFile(
    new Switch,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "--verilog")
  )

  ChiselStage.emitSystemVerilogFile(
    new LEDLoopDisplay(30000),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "--verilog")
  )
}
