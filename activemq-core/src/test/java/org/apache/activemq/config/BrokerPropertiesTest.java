begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|config
package|;
end_package

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

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|BrokerPropertiesTest
extends|extends
name|TestCase
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
name|BrokerPropertiesTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testPropertiesFile
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"properties:org/apache/activemq/config/broker.properties"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created broker: "
operator|+
name|broker
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"isUseJmx()"
argument_list|,
literal|false
argument_list|,
name|broker
operator|.
name|isUseJmx
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"isPersistent()"
argument_list|,
literal|false
argument_list|,
name|broker
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getBrokerName()"
argument_list|,
literal|"Cheese"
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

