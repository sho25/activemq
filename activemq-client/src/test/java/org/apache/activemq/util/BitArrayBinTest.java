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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|BitArrayBinTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSetAroundWindow
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSetAroundWindow
argument_list|(
literal|500
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|doTestSetAroundWindow
argument_list|(
literal|512
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|doTestSetAroundWindow
argument_list|(
literal|128
argument_list|,
literal|512
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetHiLo
parameter_list|()
throws|throws
name|Exception
block|{
name|BitArrayBin
name|toTest
init|=
operator|new
name|BitArrayBin
argument_list|(
literal|50
argument_list|)
decl_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
literal|100
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
literal|150
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"set"
argument_list|,
name|toTest
operator|.
name|getBit
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"set"
argument_list|,
name|toTest
operator|.
name|getBit
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestSetAroundWindow
parameter_list|(
name|int
name|window
parameter_list|,
name|int
name|dataSize
parameter_list|)
throws|throws
name|Exception
block|{
name|BitArrayBin
name|toTest
init|=
operator|new
name|BitArrayBin
argument_list|(
name|window
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|dataSize
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"not already set"
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|i
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"current is max"
argument_list|,
name|i
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"last is max"
argument_list|,
name|dataSize
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|windowOfValidData
init|=
name|roundWindow
argument_list|(
name|dataSize
argument_list|,
name|window
argument_list|)
decl_stmt|;
name|int
name|i
init|=
name|dataSize
decl_stmt|;
for|for
control|(
init|;
name|i
operator|>=
name|dataSize
operator|-
name|windowOfValidData
condition|;
name|i
operator|--
control|)
block|{
name|assertTrue
argument_list|(
literal|"was already set, id="
operator|+
name|i
argument_list|,
name|toTest
operator|.
name|setBit
argument_list|(
name|i
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"last is still max"
argument_list|,
name|dataSize
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|assertTrue
argument_list|(
literal|"was not already set, id="
operator|+
name|i
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|i
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
name|dataSize
operator|+
literal|1
init|;
name|j
operator|<=
operator|(
literal|2
operator|*
name|dataSize
operator|)
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"not already set: id="
operator|+
name|j
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|j
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"last still max*2"
argument_list|,
literal|2
operator|*
name|dataSize
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetUnsetAroundWindow
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSetUnSetAroundWindow
argument_list|(
literal|500
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|doTestSetUnSetAroundWindow
argument_list|(
literal|512
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|doTestSetUnSetAroundWindow
argument_list|(
literal|128
argument_list|,
literal|512
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestSetUnSetAroundWindow
parameter_list|(
name|int
name|dataSize
parameter_list|,
name|int
name|window
parameter_list|)
throws|throws
name|Exception
block|{
name|BitArrayBin
name|toTest
init|=
operator|new
name|BitArrayBin
argument_list|(
name|window
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|dataSize
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"not already set"
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|i
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|windowOfValidData
init|=
name|roundWindow
argument_list|(
name|dataSize
argument_list|,
name|window
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|dataSize
init|;
name|i
operator|>=
literal|0
operator|&&
name|i
operator|>=
name|dataSize
operator|-
name|windowOfValidData
condition|;
name|i
operator|--
control|)
block|{
name|assertTrue
argument_list|(
literal|"was already set, id="
operator|+
name|i
argument_list|,
name|toTest
operator|.
name|setBit
argument_list|(
name|i
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|dataSize
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"not already set, id:"
operator|+
name|i
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|i
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|2
operator|*
name|dataSize
init|;
name|j
operator|<
literal|4
operator|*
name|dataSize
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"not already set: id="
operator|+
name|j
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|j
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetAroundLongSizeMultiplier
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|window
init|=
literal|512
decl_stmt|;
name|int
name|dataSize
init|=
literal|1000
decl_stmt|;
for|for
control|(
name|int
name|muliplier
init|=
literal|1
init|;
name|muliplier
operator|<
literal|8
condition|;
name|muliplier
operator|++
control|)
block|{
for|for
control|(
name|int
name|value
init|=
literal|0
init|;
name|value
operator|<
name|dataSize
condition|;
name|value
operator|++
control|)
block|{
name|BitArrayBin
name|toTest
init|=
operator|new
name|BitArrayBin
argument_list|(
name|window
argument_list|)
decl_stmt|;
name|int
name|instance
init|=
name|value
operator|+
name|muliplier
operator|*
name|BitArray
operator|.
name|LONG_SIZE
decl_stmt|;
name|assertTrue
argument_list|(
literal|"not already set: id="
operator|+
name|instance
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|instance
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"not already set: id="
operator|+
name|value
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|value
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max set correct"
argument_list|,
name|instance
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLargeGapInData
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestLargeGapInData
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|doTestLargeGapInData
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestLargeGapInData
parameter_list|(
name|int
name|window
parameter_list|)
throws|throws
name|Exception
block|{
name|BitArrayBin
name|toTest
init|=
operator|new
name|BitArrayBin
argument_list|(
name|window
argument_list|)
decl_stmt|;
name|int
name|instance
init|=
name|BitArray
operator|.
name|LONG_SIZE
decl_stmt|;
name|assertTrue
argument_list|(
literal|"not already set: id="
operator|+
name|instance
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|instance
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
name|instance
operator|=
literal|12
operator|*
name|BitArray
operator|.
name|LONG_SIZE
expr_stmt|;
name|assertTrue
argument_list|(
literal|"not already set: id="
operator|+
name|instance
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|instance
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
name|instance
operator|=
literal|9
operator|*
name|BitArray
operator|.
name|LONG_SIZE
expr_stmt|;
name|assertTrue
argument_list|(
literal|"not already set: id="
operator|+
name|instance
argument_list|,
operator|!
name|toTest
operator|.
name|setBit
argument_list|(
name|instance
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLastSeq
parameter_list|()
throws|throws
name|Exception
block|{
name|BitArrayBin
name|toTest
init|=
operator|new
name|BitArrayBin
argument_list|(
literal|512
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"last not set"
argument_list|,
operator|-
literal|1
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
literal|1
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"last correct"
argument_list|,
literal|1
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
literal|64
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"last correct"
argument_list|,
literal|64
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
literal|68
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"last correct"
argument_list|,
literal|68
argument_list|,
name|toTest
operator|.
name|getLastSetIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// window moves in increments of BitArray.LONG_SIZE.
comment|// valid data window on low end can be larger than window
specifier|private
name|int
name|roundWindow
parameter_list|(
name|int
name|dataSetEnd
parameter_list|,
name|int
name|windowSize
parameter_list|)
block|{
name|int
name|validData
init|=
name|dataSetEnd
operator|-
name|windowSize
decl_stmt|;
name|int
name|validDataBin
init|=
name|validData
operator|/
name|BitArray
operator|.
name|LONG_SIZE
decl_stmt|;
name|validDataBin
operator|+=
operator|(
name|windowSize
operator|%
name|BitArray
operator|.
name|LONG_SIZE
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|int
name|startOfValid
init|=
name|validDataBin
operator|*
name|BitArray
operator|.
name|LONG_SIZE
decl_stmt|;
return|return
name|dataSetEnd
operator|-
name|startOfValid
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLargeNumber
parameter_list|()
throws|throws
name|Exception
block|{
name|BitArrayBin
name|toTest
init|=
operator|new
name|BitArrayBin
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|long
name|largeNum
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|*
literal|2L
operator|+
literal|100L
decl_stmt|;
name|toTest
operator|.
name|setBit
argument_list|(
name|largeNum
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"set"
argument_list|,
name|toTest
operator|.
name|getBit
argument_list|(
name|largeNum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

