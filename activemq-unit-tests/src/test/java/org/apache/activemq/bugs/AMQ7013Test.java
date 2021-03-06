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
name|bugs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|XATransactionId
import|;
end_import

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
name|assertNotEquals
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ7013Test
block|{
annotation|@
name|Test
specifier|public
name|void
name|hashTest
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|globalId1
init|=
name|hexStringToByteArray
argument_list|(
literal|"00000000000000000000ffff0a970616dbbe2c3b5b42f94800002259"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|branchQualifier1
init|=
name|hexStringToByteArray
argument_list|(
literal|"00000000000000000000ffff0a970616dbbe2c3b5b42f94800002259"
argument_list|)
decl_stmt|;
name|XATransactionId
name|id1
init|=
operator|new
name|XATransactionId
argument_list|()
decl_stmt|;
name|id1
operator|.
name|setGlobalTransactionId
argument_list|(
name|globalId1
argument_list|)
expr_stmt|;
name|id1
operator|.
name|setBranchQualifier
argument_list|(
name|branchQualifier1
argument_list|)
expr_stmt|;
name|id1
operator|.
name|setFormatId
argument_list|(
literal|131077
argument_list|)
expr_stmt|;
name|byte
index|[]
name|globalId2
init|=
name|hexStringToByteArray
argument_list|(
literal|"00000000000000000000ffff0a970616dbbe2c3b5b42f948000021d2"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|branchQualifier2
init|=
name|hexStringToByteArray
argument_list|(
literal|"00000000000000000000ffff0a970616dbbe2c3b5b42f948000021d2"
argument_list|)
decl_stmt|;
name|XATransactionId
name|id2
init|=
operator|new
name|XATransactionId
argument_list|()
decl_stmt|;
name|id2
operator|.
name|setGlobalTransactionId
argument_list|(
name|globalId2
argument_list|)
expr_stmt|;
name|id2
operator|.
name|setBranchQualifier
argument_list|(
name|branchQualifier2
argument_list|)
expr_stmt|;
name|id2
operator|.
name|setFormatId
argument_list|(
literal|131077
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|id1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|id2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|hexStringToByteArray
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|len
operator|/
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|data
index|[
name|i
operator|/
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|Character
operator|.
name|digit
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|,
literal|16
argument_list|)
operator|<<
literal|4
operator|)
operator|+
name|Character
operator|.
name|digit
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

