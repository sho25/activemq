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
name|transport
operator|.
name|tcp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

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
name|QualityOfServiceUtilsTest
extends|extends
name|TestCase
block|{
comment|/**      * Keeps track of the value that the System has set for the ECN bits, which      * should not be overridden when Differentiated Services is set, but may be      * overridden when Type of Service is set.      */
specifier|private
name|int
name|ECN
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Socket
name|socket
init|=
operator|new
name|Socket
argument_list|()
decl_stmt|;
name|ECN
operator|=
name|socket
operator|.
name|getTrafficClass
argument_list|()
operator|&
name|Integer
operator|.
name|parseInt
argument_list|(
literal|"00000011"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testValidDiffServIntegerValues
parameter_list|()
block|{
name|int
index|[]
name|values
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|32
block|,
literal|62
block|,
literal|63
block|}
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|values
control|)
block|{
name|testValidDiffServIntegerValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testInvalidDiffServIntegerValues
parameter_list|()
block|{
name|int
index|[]
name|values
init|=
block|{
operator|-
literal|2
block|,
operator|-
literal|1
block|,
literal|64
block|,
literal|65
block|}
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|values
control|)
block|{
name|testInvalidDiffServIntegerValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testValidDiffServNames
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|namesToExpected
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"CS0"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"000000"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"CS1"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"001000"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"CS2"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"010000"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"CS3"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"011000"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"CS4"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"100000"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"CS5"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"101000"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"CS6"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"110000"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"CS7"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"111000"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"EF"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"101110"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF11"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"001010"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF12"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"001100"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF13"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"001110"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF21"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"010010"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF22"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"010100"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF23"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"010110"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF31"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"011010"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF32"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"011100"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF33"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"011110"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF41"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"100010"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF42"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"100100"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|namesToExpected
operator|.
name|put
argument_list|(
literal|"AF43"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|"100110"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|namesToExpected
operator|.
name|keySet
argument_list|()
control|)
block|{
name|testValidDiffServName
argument_list|(
name|name
argument_list|,
name|namesToExpected
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testInvalidDiffServNames
parameter_list|()
block|{
name|String
index|[]
name|names
init|=
block|{
literal|"hello_world"
block|,
literal|""
block|,
literal|"abcd"
block|}
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|testInvalidDiffServName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testValidDiffServName
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|expected
parameter_list|)
block|{
name|int
name|dscp
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|dscp
operator|=
name|QualityOfServiceUtils
operator|.
name|getDSCP
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IllegalArgumentException thrown for valid Differentiated "
operator|+
literal|" Services name: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
comment|// Make sure it adjusted for any system ECN values.
name|assertEquals
argument_list|(
literal|"Incorrect Differentiated Services Code Point "
operator|+
name|dscp
operator|+
literal|" returned for name "
operator|+
name|name
operator|+
literal|"."
argument_list|,
name|ECN
operator||
operator|(
name|expected
operator|<<
literal|2
operator|)
argument_list|,
name|dscp
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testInvalidDiffServName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|QualityOfServiceUtils
operator|.
name|getDSCP
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No IllegalArgumentException thrown for invalid Differentiated"
operator|+
literal|" Services value: "
operator|+
name|name
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{         }
block|}
specifier|private
name|void
name|testValidDiffServIntegerValue
parameter_list|(
name|int
name|val
parameter_list|)
block|{
try|try
block|{
name|int
name|dscp
init|=
name|QualityOfServiceUtils
operator|.
name|getDSCP
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
decl_stmt|;
comment|// Make sure it adjusted for any system ECN values.
name|assertEquals
argument_list|(
literal|"Incorrect Differentiated Services Code Point "
operator|+
literal|"returned for value "
operator|+
name|val
operator|+
literal|"."
argument_list|,
name|ECN
operator||
operator|(
name|val
operator|<<
literal|2
operator|)
argument_list|,
name|dscp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IllegalArgumentException thrown for valid Differentiated "
operator|+
literal|"Services value "
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testInvalidDiffServIntegerValue
parameter_list|(
name|int
name|val
parameter_list|)
block|{
try|try
block|{
name|QualityOfServiceUtils
operator|.
name|getDSCP
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No IllegalArgumentException thrown for invalid "
operator|+
literal|"Differentiated Services value "
operator|+
name|val
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|testValidTypeOfServiceValues
parameter_list|()
block|{
name|int
index|[]
name|values
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|32
block|,
literal|100
block|,
literal|255
block|}
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|values
control|)
block|{
name|testValidTypeOfServiceValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testInvalidTypeOfServiceValues
parameter_list|()
block|{
name|int
index|[]
name|values
init|=
block|{
operator|-
literal|2
block|,
operator|-
literal|1
block|,
literal|256
block|,
literal|257
block|}
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|values
control|)
block|{
name|testInvalidTypeOfServiceValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testValidTypeOfServiceValue
parameter_list|(
name|int
name|val
parameter_list|)
block|{
try|try
block|{
name|int
name|typeOfService
init|=
name|QualityOfServiceUtils
operator|.
name|getToS
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect Type of Services value returned for "
operator|+
name|val
operator|+
literal|"."
argument_list|,
name|val
argument_list|,
name|typeOfService
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IllegalArgumentException thrown for valid Type of Service "
operator|+
literal|"value "
operator|+
name|val
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testInvalidTypeOfServiceValue
parameter_list|(
name|int
name|val
parameter_list|)
block|{
try|try
block|{
name|QualityOfServiceUtils
operator|.
name|getToS
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No IllegalArgumentException thrown for invalid "
operator|+
literal|"Type of Service value "
operator|+
name|val
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{         }
block|}
block|}
end_class

end_unit

