import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import pipeline.REG_IF_ID

class PipelineSpec extends AnyFreeSpec with Matchers {
  "REG_IF_ID" in {
    simulate(new REG_IF_ID) { dut =>
        dut.signal.stop.poke(false.B)
        dut.signal.flush.poke(false.B)
        dut.in_data.pc.poke(0.U)
        dut.in_data.inst.poke(0.U)
        dut.clock.step()
        dut.out_data.pc.expect(0.U)
        dut.out_data.inst.expect(0.U)

        dut.in_data.pc.poke(1.U)
        dut.in_data.inst.poke(1.U)
        dut.clock.step()
        dut.out_data.pc.expect(1.U)
        dut.out_data.inst.expect(1.U)

        dut.signal.stop.poke(true.B)
        dut.in_data.pc.poke(2.U)
        dut.in_data.inst.poke(2.U)
        dut.clock.step()
        dut.out_data.pc.expect(1.U)
        dut.out_data.inst.expect(1.U)

        dut.signal.stop.poke(false.B)
        dut.signal.flush.poke(true.B)
        dut.in_data.pc.poke(3.U)
        dut.in_data.inst.poke(3.U)
        dut.clock.step()
        dut.out_data.pc.expect(0.U)
        dut.out_data.inst.expect(0.U)
    }
  }
}
