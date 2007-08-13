begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
package|;
end_package

begin_comment
comment|/**  * MBean used to manage all of the TransportLoggers at once.  * Avalaible operations:  *  -Enable logging for all TransportLoggers at once.  *  -Disable logging for all TransportLoggers at once.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TransportLoggerControlMBean
block|{
comment|/**      * Enable logging for all Transport Loggers at once.      */
specifier|public
name|void
name|enableAllTransportLoggers
parameter_list|()
function_decl|;
comment|/**      * Disable logging for all Transport Loggers at once.      */
specifier|public
name|void
name|disableAllTransportLoggers
parameter_list|()
function_decl|;
comment|/**      * Reloads log4j.properties from the classpath      * @throws Exception      */
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

