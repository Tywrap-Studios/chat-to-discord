package org.tywrapstudios.krafter.i18n

import dev.kordex.core.i18n.types.Bundle
import dev.kordex.core.i18n.types.Key

public object Translations {
  public val bundle: Bundle = Bundle("krafter.strings")

  public object Enum {
    public object Suggestions {
      public object Status {
        /**
         * Approved
         */
        public val approved: Key = Key("enum.suggestions.status.approved")
            .withBundle(Translations.bundle)

        /**
         * Denied
         */
        public val denied: Key = Key("enum.suggestions.status.denied")
            .withBundle(Translations.bundle)

        /**
         * Duplicate
         */
        public val duplicate: Key = Key("enum.suggestions.status.duplicate")
            .withBundle(Translations.bundle)

        /**
         * Future Concern
         */
        public val future: Key = Key("enum.suggestions.status.future")
            .withBundle(Translations.bundle)

        /**
         * Implemented
         */
        public val implemented: Key = Key("enum.suggestions.status.implemented")
            .withBundle(Translations.bundle)

        /**
         * Invalid
         */
        public val invalid: Key = Key("enum.suggestions.status.invalid")
            .withBundle(Translations.bundle)

        /**
         * Open
         */
        public val `open`: Key = Key("enum.suggestions.status.open")
            .withBundle(Translations.bundle)

        /**
         * Requires Name
         */
        public val requiresName: Key = Key("enum.suggestions.status.requires_name")
            .withBundle(Translations.bundle)

        /**
         * Spam
         */
        public val spam: Key = Key("enum.suggestions.status.spam")
            .withBundle(Translations.bundle)

        /**
         * Stale
         */
        public val stale: Key = Key("enum.suggestions.status.stale")
            .withBundle(Translations.bundle)
      }
    }
  }
}
