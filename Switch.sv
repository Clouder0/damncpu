// Generated by CIRCT firtool-1.62.0
module Switch(
  input         clock,
                reset,
  input  [11:0] in_addr,
  input  [23:0] in_switch,
  output [31:0] out_rdata
);

  assign out_rdata = reset ? 32'h0 : {8'h0, in_switch};
endmodule

