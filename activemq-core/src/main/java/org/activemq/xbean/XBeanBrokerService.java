begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|xbean
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|DisposableBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|InitializingBean
import|;
end_import

begin_comment
comment|/**  * Represents a running broker service which consists of a number of transport  * connectors, network connectors and a bunch of properties which can be used to  * configure the broker as its lazily created.  *   * @org.xbean.XBean element="broker" rootElement="true" description="An ActiveMQ  *                  Message Broker which consists of a number of transport  *                  connectors, network connectors and a persistence adaptor"  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|XBeanBrokerService
extends|extends
name|BrokerService
implements|implements
name|InitializingBean
implements|,
name|DisposableBean
block|{
specifier|private
name|boolean
name|start
init|=
literal|true
decl_stmt|;
specifier|public
name|XBeanBrokerService
parameter_list|()
block|{     }
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|start
condition|)
block|{
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
comment|/**      * Sets whether or not the broker is started along with the ApplicationContext it is defined within.      * Normally you would want the broker to start up along with the ApplicationContext but sometimes when working      * with JUnit tests you may wish to start and stop the broker explicitly yourself.      */
specifier|public
name|void
name|setStart
parameter_list|(
name|boolean
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
block|}
end_class

end_unit

