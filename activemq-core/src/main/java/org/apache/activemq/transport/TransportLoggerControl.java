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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
operator|.
name|BrokerView
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
operator|.
name|ManagementContext
import|;
end_import

begin_comment
comment|/**  * Implementation of the TransportLoggerControlMBean interface,  * which is an MBean used to control all TransportLoggers at once.  */
end_comment

begin_class
specifier|public
class|class
name|TransportLoggerControl
implements|implements
name|TransportLoggerControlMBean
block|{
comment|/**      * Constructor      */
specifier|public
name|TransportLoggerControl
parameter_list|(
name|ManagementContext
name|managementContext
parameter_list|)
block|{     }
comment|// doc comment inherited from TransportLoggerControlMBean
specifier|public
name|void
name|disableAllTransportLoggers
parameter_list|()
block|{
name|TransportLoggerView
operator|.
name|disableAllTransportLoggers
argument_list|()
expr_stmt|;
block|}
comment|// doc comment inherited from TransportLoggerControlMBean
specifier|public
name|void
name|enableAllTransportLoggers
parameter_list|()
block|{
name|TransportLoggerView
operator|.
name|enableAllTransportLoggers
argument_list|()
expr_stmt|;
block|}
comment|//  doc comment inherited from TransportLoggerControlMBean
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|BrokerView
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|reloadLog4jProperties
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

