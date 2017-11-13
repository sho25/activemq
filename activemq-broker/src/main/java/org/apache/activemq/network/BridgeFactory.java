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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|Transport
import|;
end_import

begin_comment
comment|/**  * Encapsulation of bridge creation logic.  *  * This SPI interface is intended to customize or decorate existing bridge implementations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BridgeFactory
block|{
comment|/**      * Create a network bridge between two specified transports.      *      * @param configuration Bridge configuration.      * @param localTransport Local side of bridge.      * @param remoteTransport Remote side of bridge.      * @param listener Bridge listener.      * @return the NetworkBridge      */
name|DemandForwardingBridge
name|createNetworkBridge
parameter_list|(
name|NetworkBridgeConfiguration
name|configuration
parameter_list|,
name|Transport
name|localTransport
parameter_list|,
name|Transport
name|remoteTransport
parameter_list|,
specifier|final
name|NetworkBridgeListener
name|listener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

