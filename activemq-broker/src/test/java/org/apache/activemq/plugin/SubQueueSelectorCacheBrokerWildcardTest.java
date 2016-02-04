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
name|plugin
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests that presence of wildcard characters is correctly identified by SubQueueSelectorCacheBroker  */
end_comment

begin_class
specifier|public
class|class
name|SubQueueSelectorCacheBrokerWildcardTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSimpleWildcardEvaluation
parameter_list|()
block|{
name|assertWildcard
argument_list|(
literal|true
argument_list|,
literal|"modelInstanceId = '170' AND modelClassId LIKE 'com.whatever.something.%'"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|true
argument_list|,
literal|"JMSMessageId LIKE '%'"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|false
argument_list|,
literal|"modelClassId = 'com.whatever.something.%'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEscapedWildcardEvaluation
parameter_list|()
block|{
name|assertWildcard
argument_list|(
literal|true
argument_list|,
literal|"foo LIKE '!_%' ESCAPE '!'"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|false
argument_list|,
literal|"_foo__ LIKE '!_!%' ESCAPE '!'"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|true
argument_list|,
literal|"_foo_ LIKE '_%' ESCAPE '.'"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|true
argument_list|,
literal|"JMSMessageId LIKE '%' ESCAPE '.'"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|false
argument_list|,
literal|"_foo_ LIKE '\\_\\%' ESCAPE '\\'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonWildard
parameter_list|()
block|{
name|assertWildcard
argument_list|(
literal|false
argument_list|,
literal|"type = 'UPDATE_ENTITY'"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|false
argument_list|,
literal|"a_property = 1"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|false
argument_list|,
literal|"percentage = '100%'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testApostrophes
parameter_list|()
block|{
name|assertWildcard
argument_list|(
literal|true
argument_list|,
literal|"quote LIKE '''In G_d We Trust'''"
argument_list|)
expr_stmt|;
name|assertWildcard
argument_list|(
literal|true
argument_list|,
literal|"quote LIKE '''In Gd We Trust''' OR quote not like '''In G_d We Trust'''"
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|assertWildcard
parameter_list|(
name|boolean
name|expected
parameter_list|,
name|String
name|selector
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Wildcard should "
operator|+
operator|(
operator|!
name|expected
condition|?
literal|" NOT "
else|:
literal|""
operator|)
operator|+
literal|" be found in "
operator|+
name|selector
argument_list|,
name|expected
argument_list|,
name|SubQueueSelectorCacheBroker
operator|.
name|hasWildcards
argument_list|(
name|selector
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

