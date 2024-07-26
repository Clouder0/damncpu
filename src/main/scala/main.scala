import chisel3._
import data._
import circt.stage.ChiselStage
import chisel3.experimental.hierarchy.Instantiate
import mock.DRAM
import chisel3.stage.ChiselGeneratorAnnotation
import misc.Switch
import misc.LEDLoopDisplay

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
