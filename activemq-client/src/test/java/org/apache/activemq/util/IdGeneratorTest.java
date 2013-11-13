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
name|util
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

begin_class
specifier|public
class|class
name|IdGeneratorTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testSanitizeHostName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"somehost.lan"
argument_list|,
name|IdGenerator
operator|.
name|sanitizeHostName
argument_list|(
literal|"somehost.lan"
argument_list|)
argument_list|)
expr_stmt|;
comment|// include a UTF-8 char in the text \u0E08 is a Thai elephant
name|assertEquals
argument_list|(
literal|"otherhost.lan"
argument_list|,
name|IdGenerator
operator|.
name|sanitizeHostName
argument_list|(
literal|"other\u0E08host.lan"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

