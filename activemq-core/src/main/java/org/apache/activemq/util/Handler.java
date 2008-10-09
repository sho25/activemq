begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_interface
specifier|public
interface|interface
name|Handler
parameter_list|<
name|T
parameter_list|>
block|{
name|void
name|handle
parameter_list|(
name|T
name|e
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

