begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * Abstraction that allows you to control which brokers a NetworkConnector connects bridges to.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConnectionFilter
block|{
comment|/**      * @param location      * @return true if the network connector should establish a connection to the specified location.      */
name|boolean
name|connectTo
parameter_list|(
name|URI
name|location
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

