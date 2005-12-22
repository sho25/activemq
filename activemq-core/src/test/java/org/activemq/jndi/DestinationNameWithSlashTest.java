begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|jndi
package|;
end_package

begin_comment
comment|/**  * Test case for AMQ-140  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|DestinationNameWithSlashTest
extends|extends
name|JNDITestSupport
block|{
specifier|public
name|void
name|testNameWithSlash
parameter_list|()
throws|throws
name|Exception
block|{
name|assertDestinationExists
argument_list|(
literal|"jms/Queue"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|configureEnvironment
parameter_list|()
block|{
name|super
operator|.
name|configureEnvironment
argument_list|()
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
literal|"queue.jms/Queue"
argument_list|,
literal|"example.myqueue"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

