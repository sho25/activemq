begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|usecases
package|;
end_package

begin_class
specifier|public
class|class
name|NonPersistentDurableTopicSystemTest
extends|extends
name|SystemTestSupport
block|{
comment|/**      * Unit test for non-persistent durable topic messages with the following settings:      * 1 Producer, 1 Consumer, 1 Subject, 10 Messages      *      * @throws Exception      */
specifier|public
name|void
name|testNonPersistentDurableTopicMessageA
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|"testNonPersistentDurableTopicMessageA()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unit test for non-persistent durable topic messages with the following settings:      * 10 Producers, 10 Consumers, 1 Subject, 10 Messages      *      * @throws Exception      */
specifier|public
name|void
name|testNonPersistentDurableTopicMessageB
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|"testNonPersistentDurableTopicMessageB()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unit test for non-persistent durable topic messages with the following settings:      * 10 Producers, 10 Consumers, 10 Subjects, 10 Messages      *      * @throws Exception      */
specifier|public
name|void
name|testNonPersistentDurableTopicMessageC
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|"testNonPersistentDurableTopicMessageC()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

