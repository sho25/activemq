begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|xbean
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|BrokerFactory
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
name|BrokerService
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
name|util
operator|.
name|JMXSupport
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ManagementContextXBeanConfigTest
extends|extends
name|TestCase
block|{
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|public
name|void
name|testManagmentContextConfiguredCorrectly
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|2011
argument_list|,
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getConnectorPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test.domain"
argument_list|,
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getJmxDomainName
argument_list|()
argument_list|)
expr_stmt|;
name|MBeanServer
name|beanServer
init|=
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getMBeanServer
argument_list|()
decl_stmt|;
comment|// Make sure the broker is registered in the right jmx domain.
name|Hashtable
name|map
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"Type"
argument_list|,
literal|"Broker"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"BrokerName"
argument_list|,
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
literal|"localhost"
argument_list|)
argument_list|)
expr_stmt|;
name|ObjectName
name|on
init|=
operator|new
name|ObjectName
argument_list|(
literal|"test.domain"
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|Object
name|value
init|=
name|beanServer
operator|.
name|getAttribute
argument_list|(
name|on
argument_list|,
literal|"TotalEnqueueCount"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
literal|"org/apache/activemq/xbean/management-context-test.xml"
decl_stmt|;
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:"
operator|+
name|uri
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

