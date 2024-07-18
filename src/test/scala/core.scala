import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class CoreSpec extends AnyFreeSpec with Matchers {
  "Set x1, 3; Set x2, 1; add x3, x1, x2" in {
    simulate(new MockCPU) { dut =>
      dut.in.inst.poke("h00306093".U)
      dut.clock.step()
      dut.out.alu_res.expect(3.U)

      dut.in.inst.poke("b00000000000100000110000100010011".U)
      dut.clock.step()
      dut.out.alu_res.expect(1.U)

      dut.in.inst.poke("b00000000001000001000000110110011".U)
      dut.clock.step()
      dut.out.alu_res.expect(4.U)
    }
  }
  
  "Jump" in {
    simulate(new MockCPU) { dut => 
      dut.in.inst.poke("h0040006f".U)
      dut.clock.step()
      dut.out.inst_addr.expect(0.U)
      dut.clock.step()
      dut.out.inst_addr.expect(4.U)
    }
  }
}
