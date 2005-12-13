begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_class
specifier|public
class|class
name|JMXSupport
block|{
specifier|static
specifier|public
name|String
name|encodeObjectNamePart
parameter_list|(
name|String
name|part
parameter_list|)
block|{
return|return
name|ObjectName
operator|.
name|quote
argument_list|(
name|part
argument_list|)
return|;
comment|/*         String answer = part.replaceAll("[\\:\\,\\'\\\"]", "_");         answer = answer.replaceAll("\\?", "&qe;");         answer = answer.replaceAll("=", "&amp;");         return answer;         */
block|}
block|}
end_class

end_unit

