package com.gjgs.gjgs.modules.member.dto.myinfo;

import com.gjgs.gjgs.modules.member.validator.CheckIsExistZone;
import lombok.*;

import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneModifyRequest {

    //@Pattern(regexp = "^[0-9]*$")
    @Min(1)
    @CheckIsExistZone
    private Long zoneId;
}
