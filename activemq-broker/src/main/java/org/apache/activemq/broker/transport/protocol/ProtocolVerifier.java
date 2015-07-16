begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|transport
operator|.
name|protocol
package|;
end_package

begin_interface
specifier|public
interface|interface
name|ProtocolVerifier
block|{
specifier|public
name|boolean
name|isProtocol
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

