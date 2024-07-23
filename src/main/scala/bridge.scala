import data._

import chisel3._
import chisel3.util.Fill
import chisel3.experimental.hierarchy.instantiable
import chisel3.experimental.hierarchy.public

@instantiable
class Bridge extends Module {
  @public val cpu = IO(new Bundle {
    val we = Input(Bool())
    val addr = Input(UInt(32.W))
    val data_from_cpu = Input(UInt(32.W))
    val data_to_cpu = Output(UInt(32.W))
  })

  @public val dram = IO(new Bundle {
    val we = Output(Bool())
    val addr = Output(UInt(32.W))
    val data_from_dram = Input(UInt(32.W))
    val data_to_dram = Output(UInt(32.W))
  })

  @public val digit = IO(new Bundle {
    val addr_to_digit = Output(UInt(12.W))
    val we_to_digit = Output(Bool())
    val wdata_to_digit = Output(UInt(32.W))
  })
  
  @public val led = IO(new Bundle {
    val addr_to_led = Output(UInt(12.W))
    val we_to_led = Output(Bool())
    val wdata_to_led = Output(UInt(32.W))
  })

  @public val switches = IO(new Bundle {
    val addr_to_switches = Output(UInt(12.W))
    val rdata_from_switches = Input(UInt(32.W))
  })

  
  @public val button = IO(new Bundle {
    val addr_to_button = Output(UInt(12.W))
    val rdata_from_button = Input(UInt(32.W))
  })

  val access_digit = Wire(Bool())
  val access_led = Wire(Bool())
  val access_switch = Wire(Bool())
  val access_button = Wire(Bool())
  val access_mem = Wire(Bool())
  access_mem := ! (cpu.addr(31, 12) === Fill(20, 1.U))
  access_digit := (cpu.addr(31, 12) === "hFFFFF000".U)
  access_led := (cpu.addr(31, 12) === "hFFFFF060".U)
  access_switch := (cpu.addr(31, 12) === "hFFFFF070".U)
  access_button := (cpu.addr(31, 12) === "hFFFFF078".U)

  dram.addr := cpu.addr
  dram.we := cpu.we && access_mem
  dram.data_to_dram := cpu.data_from_cpu
  
  digit.addr_to_digit := cpu.addr(11, 0)
  digit.we_to_digit := cpu.we && access_digit
  digit.wdata_to_digit := cpu.data_from_cpu

  led.addr_to_led := cpu.addr(11, 0)
  led.we_to_led := cpu.we && access_led
  led.wdata_to_led := cpu.data_from_cpu

  switches.addr_to_switches := cpu.addr(11, 0)
  button.addr_to_button := cpu.addr(11, 0)

  when(access_mem) {
    cpu.data_to_cpu := dram.data_from_dram
  }.elsewhen(access_switch) {
    cpu.data_to_cpu := switches.rdata_from_switches
  }.elsewhen(access_button) {
    cpu.data_to_cpu := button.rdata_from_button
  }.otherwise {
    cpu.data_to_cpu := Fill(32, 1.U)
  }
}
