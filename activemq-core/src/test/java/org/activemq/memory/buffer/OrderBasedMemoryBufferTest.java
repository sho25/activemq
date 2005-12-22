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
name|memory
operator|.
name|buffer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|memory
operator|.
name|buffer
operator|.
name|MessageBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|memory
operator|.
name|buffer
operator|.
name|OrderBasedMessageBuffer
import|;
end_import

begin_comment
comment|/**  *  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|OrderBasedMemoryBufferTest
extends|extends
name|MemoryBufferTestSupport
block|{
specifier|public
name|void
name|testSizeWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|qA
operator|.
name|add
argument_list|(
name|createMessage
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|qB
operator|.
name|add
argument_list|(
name|createMessage
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|qB
operator|.
name|add
argument_list|(
name|createMessage
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|qC
operator|.
name|add
argument_list|(
name|createMessage
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|dump
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"buffer size"
argument_list|,
literal|40
argument_list|,
name|buffer
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qA"
argument_list|,
literal|10
argument_list|,
name|qA
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qB"
argument_list|,
literal|20
argument_list|,
name|qB
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qC"
argument_list|,
literal|10
argument_list|,
name|qC
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|qC
operator|.
name|add
argument_list|(
name|createMessage
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|dump
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"buffer size"
argument_list|,
literal|40
argument_list|,
name|buffer
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qA"
argument_list|,
literal|0
argument_list|,
name|qA
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qB"
argument_list|,
literal|20
argument_list|,
name|qB
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qC"
argument_list|,
literal|20
argument_list|,
name|qC
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|qB
operator|.
name|add
argument_list|(
name|createMessage
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|dump
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"buffer size"
argument_list|,
literal|40
argument_list|,
name|buffer
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qA"
argument_list|,
literal|0
argument_list|,
name|qA
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qB"
argument_list|,
literal|20
argument_list|,
name|qB
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qC"
argument_list|,
literal|20
argument_list|,
name|qC
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|qA
operator|.
name|add
argument_list|(
name|createMessage
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|dump
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"buffer size"
argument_list|,
literal|40
argument_list|,
name|buffer
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qA"
argument_list|,
literal|10
argument_list|,
name|qA
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qB"
argument_list|,
literal|10
argument_list|,
name|qB
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"qC"
argument_list|,
literal|20
argument_list|,
name|qC
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MessageBuffer
name|createMessageBuffer
parameter_list|()
block|{
return|return
operator|new
name|OrderBasedMessageBuffer
argument_list|(
literal|40
argument_list|)
return|;
block|}
block|}
end_class

end_unit

