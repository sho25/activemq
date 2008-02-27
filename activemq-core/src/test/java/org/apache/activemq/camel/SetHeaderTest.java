begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|camel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|mock
operator|.
name|MockEndpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|EndpointInject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|CamelContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Exchange
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|util
operator|.
name|ObjectHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|test
operator|.
name|context
operator|.
name|ContextConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|test
operator|.
name|context
operator|.
name|junit38
operator|.
name|AbstractJUnit38SpringContextTests
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
name|annotation
operator|.
name|Autowired
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
annotation|@
name|ContextConfiguration
specifier|public
class|class
name|SetHeaderTest
extends|extends
name|AbstractJUnit38SpringContextTests
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SetHeaderTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Autowired
specifier|protected
name|CamelContext
name|camelContext
decl_stmt|;
annotation|@
name|EndpointInject
argument_list|(
name|uri
operator|=
literal|"mock:results"
argument_list|)
specifier|protected
name|MockEndpoint
name|expectedEndpoint
decl_stmt|;
specifier|public
name|void
name|testMocksAreValid
parameter_list|()
throws|throws
name|Exception
block|{
comment|// lets add more expectations
name|expectedEndpoint
operator|.
name|expectedMessageCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expectedEndpoint
operator|.
name|message
argument_list|(
literal|0
argument_list|)
operator|.
name|header
argument_list|(
literal|"JMSXGroupID"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ABC"
argument_list|)
expr_stmt|;
name|MockEndpoint
operator|.
name|assertIsSatisfied
argument_list|(
name|camelContext
argument_list|)
expr_stmt|;
comment|// lets dump the received messages
name|List
argument_list|<
name|Exchange
argument_list|>
name|list
init|=
name|expectedEndpoint
operator|.
name|getReceivedExchanges
argument_list|()
decl_stmt|;
for|for
control|(
name|Exchange
name|exchange
range|:
name|list
control|)
block|{
name|Object
name|body
init|=
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|getBody
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received: body: "
operator|+
name|body
operator|+
literal|" of type: "
operator|+
name|ObjectHelper
operator|.
name|className
argument_list|(
name|body
argument_list|)
operator|+
literal|" on: "
operator|+
name|exchange
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

