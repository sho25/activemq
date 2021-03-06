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
name|broker
operator|.
name|partition
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
name|activemq
operator|.
name|partition
operator|.
name|PartitionBrokerPlugin
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
name|partition
operator|.
name|dto
operator|.
name|Partitioning
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|SpringPartitionBrokerTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testCreatePartitionBroker
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
literal|"xbean:activemq-partition.xml"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|broker
operator|.
name|getPlugins
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|PartitionBrokerPlugin
name|plugin
init|=
operator|(
name|PartitionBrokerPlugin
operator|)
name|broker
operator|.
name|getPlugins
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Partitioning
name|config
init|=
name|plugin
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|config
operator|.
name|getBrokers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|o
decl_stmt|;
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|"  \"by_client_id\":{\n"
operator|+
literal|"    \"client1\":{\"ids\":[\"broker1\"]},\n"
operator|+
literal|"    \"client2\":{\"ids\":[\"broker1\",\"broker2\"]}\n"
operator|+
literal|"  },\n"
operator|+
literal|"  \"brokers\":{\n"
operator|+
literal|"    \"broker1\":\"tcp://localhost:61616\",\n"
operator|+
literal|"    \"broker2\":\"tcp://localhost:61616\"\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|Partitioning
name|expected
init|=
name|Partitioning
operator|.
name|MAPPER
operator|.
name|readValue
argument_list|(
name|json
argument_list|,
name|Partitioning
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|toString
argument_list|()
argument_list|,
name|config
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

