import chisel3._
import data._
import circt.stage.ChiselStage
import chisel3.experimental.hierarchy.Instantiate
import mock.DRAM

class MockCPU extends Module {
    val core = Instantiate(new CPU)
    val dram = Instantiate(new DRAM)
    val in = IO(new Bundle {
        val inst = Input(UInt(32.W))
    })
    val out = IO(new Bundle {
        val inst_addr = Output(UInt(16.W))
        val alu_res = Output(UInt(32.W))
    })
    core.io_irom.inst := in.inst
    out.inst_addr := core.io_irom.inst_addr
    dram.in.addr := core.io_dram.alu_res
    dram.in.din := core.io_dram.rD2
    dram.in.we := core.io_dram.we
    core.io_dram.dram_rdata := dram.out.dout
    
    out.alu_res := core.io_dram.alu_res
}

object Main extends App {
  ChiselStage.emitSystemVerilogFile(
    new CPU,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}
