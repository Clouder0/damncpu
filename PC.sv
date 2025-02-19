// Generated by CIRCT firtool-1.62.0
module PC(
  input         clock,
                reset,
  input  [31:0] io_din,
  output [31:0] io_pc
);

  reg [31:0] output_0;
  always @(posedge clock) begin
    if (reset)
      output_0 <= 32'h0;
    else
      output_0 <= io_din;
  end // always @(posedge)
  assign io_pc = output_0;
endmodule

